package arthur.feedingControl.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;

import org.apache.log4j.Logger;

import arthur.feedingControl.device.DeviceControl;
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class SerialPortHandler implements  SerialPortEventListener{
	static Logger log = Logger.getLogger(SerialPortHandler.class);
	private  String comName = null; // 配置文件
	
    // 检测系统中可用的通讯端口类
    private CommPortIdentifier portId =null;
    
    // 枚举类型
    private Enumeration<CommPortIdentifier> portList =null;
    
    // RS485串口
    private SerialPort serialPort = null;
    
    // 输入输出流
    private InputStream inputStream =null;
    private OutputStream outputStream =null;
   
    private Timer aft = null;
    private byte[] aftCacheData = null;
    
    private SerialPortHandler() {}
    
    public static SerialPortHandler getInstance(String comportname) {
    	if(comportname == null || comportname.isEmpty())return null;
    	
    	SerialPortHandler object = new SerialPortHandler();
    	object.comName = comportname;
    	object.init();
    	return object;
    }
 // 初始化串口
    @SuppressWarnings("unchecked")
    public void init() {
        // 获取系统中所有的通讯端口
        portList = CommPortIdentifier.getPortIdentifiers();
        // 循环通讯端口
        while (portList.hasMoreElements()) {
            portId = portList.nextElement();
            // 判断是否是串口
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
            	
                // 比较串口名称是否是"COM3"
            	log.info("comport:"+portId.getName());
                if (comName.equals(portId.getName())) {
                    System.out.println("find com"+comName);
                    // 打开串口
                    try {
                        // open:（应用程序名【随意命名】，阻塞时等待的毫秒数）
                        serialPort = (SerialPort) portId.open(comName, 2000);
                        //实例化输入流
                        inputStream = serialPort.getInputStream();
                        // 设置串口监听
                        serialPort.addEventListener(this);
                        // 设置串口数据时间有效(可监听)
                        serialPort.notifyOnDataAvailable(true);
                        // 设置串口通讯参数
                        // 波特率，数据位，停止位和校验方式
                        serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, //
                                SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                        
                    } catch (PortInUseException e) {
                        e.printStackTrace();
                    } catch (TooManyListenersException e) {
                        e.printStackTrace();
                    } catch (UnsupportedCommOperationException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    // 实现接口SerialPortEventListener中的方法 读取从串口中接收的数据
    @Override
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
        case SerialPortEvent.BI: // 通讯中断
        	log.info("bi");break;
        case SerialPortEvent.OE: // 溢位错误
        	log.info("oe");break;
        case SerialPortEvent.FE: // 帧错误
        	log.info("fe");break;
        case SerialPortEvent.PE: // 奇偶校验错误
        	log.info("pe");break;
        case SerialPortEvent.CD: // 载波检测
        	log.info("cd");break;
        case SerialPortEvent.CTS: // 清除发送
        	log.info("cts");break;
        case SerialPortEvent.DSR: // 数据设备准备好
        	log.info("dsr");break;
        case SerialPortEvent.RI: // 响铃侦测
        	log.info("ri");break;
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 输出缓冲区已清空
        	log.info("OUTPUT_BUFFER_EMPTY");break;
        case SerialPortEvent.DATA_AVAILABLE: // 有数据到达
        	log.info("DATA_AVAILABLE");
            readComm();
            break;
        default:
        	log.info("default event");
            break;
        }
    }

    // 读取串口返回信息
    public void readComm() {
    	changeWaitFlag(null);
    	try {
			Thread.sleep(100);
			// 等待数据完全到达。
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
        byte[] readBuffer = new byte[1024];
        try {
            inputStream = serialPort.getInputStream();
            // 从线路上读取数据流
            int len = 0;
            String test ="";
            if((len = inputStream.read(readBuffer)) != -1) {
            	for(int i =0 ;i <len;i++ ) {
            		Byte one = readBuffer[i];
            		String byteToHex = byteToHex(one);
            		test+= byteToHex;
            	}
            }
            byte[] receiveData = new byte[len];
            System.arraycopy(readBuffer,0,receiveData, 0,len);
            DeviceControl.notifyThis(receiveData);
            log.info("receive data:"+test);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void changeWaitFlag(byte[] data) { // 必须成对出现     sendMsg和readcomm 
    	aftCacheData = data;
    	if(aft !=null) { // 清空
    		aft.cancel();
    		aft = null;
    	}else {
    		aft = new Timer();
    		aft.schedule(new TimerTask() { // 3秒后如果没有回复，就是回复超时。
				@Override
				public void run() {
					//某一台设备响应超时。
					aft = null;
					deviceTimeOutError();
					DeviceControl.notifyThis(null);
				}
			}, 3000); 
    	}
    }
    private void deviceTimeOutError() {
    	LogService ls = new LogServiceImp();
    	String rsaddr = byteToHex(aftCacheData[1]);
		String logtext = portId.getName()+","+rsaddr+",电机控制板响应超时，请联系技术人员。";
		ls.AddLost("error",logtext );
    }
    //向串口发送数据
    // 所有的串口消息都会等等回复。
    public void sendMsg(byte[] data){
        try {
            //实例化输出流
        	if(outputStream == null) 
        		outputStream = serialPort.getOutputStream();
        } catch (IOException e) {
            log.error("get outputstream error",e);
        }
        try {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e);
        }
        changeWaitFlag(data);
        try{
        	String test = "";
        	for(int i =0 ;i <data.length;i++ ) {
        		Byte one = data[i];
        		String byteToHex = byteToHex(one);
        		test+= byteToHex;
        	}
        	log.info("send data:"+test);
        }catch(Exception e){
        }
    }
    public  static String byteToHex(byte b){  
        String hex = Integer.toHexString(b & 0xFF);  
        if(hex.length() < 2){  
            hex = "0" + hex;  
        }  
        return hex;  
    }
    public static void main(String[] args) {
		int a = 255;
		byte b = (byte)a;
		String byteToHex = byteToHex(b);
		System.out.println(byteToHex);
	}
}
