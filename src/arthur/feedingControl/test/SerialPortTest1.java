package arthur.feedingControl.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;
 
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
 
public class SerialPortTest1 implements  SerialPortEventListener {
	private  String comName = "COM3"; // 配置文件
	
    // 检测系统中可用的通讯端口类
    private CommPortIdentifier portId;
    // 枚举类型
    private Enumeration<CommPortIdentifier> portList;
 
    // RS485串口
    private SerialPort serialPort;
    
    // 输入输出流
    private InputStream inputStream;
    private OutputStream outputStream;
 
    // 保存串口返回信息
    
    // 单例创建
//    private static SerialPortTest1 uniqueInstance = new SerialPortTest1();
    
    private SerialPortTest1() {
	}
    public static SerialPortTest1 getInstance(String comName) {
    	SerialPortTest1 uniqueInstance = new SerialPortTest1();
    	uniqueInstance.init();
    	return uniqueInstance;
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
//            	System.out.println(portId.getName());
                if (comName.equals(portId.getName())) {
                    System.out.println("找到串口"+comName);
                    // 打开串口
                    try {
                        // open:（应用程序名【随意命名】，阻塞时等待的毫秒数）
                        serialPort = (SerialPort) portId.open(Object.class.getSimpleName(), 2000);
                        System.out.println("获取到串口对象,"+comName);
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
        case SerialPortEvent.OE: // 溢位错误
        case SerialPortEvent.FE: // 帧错误
        case SerialPortEvent.PE: // 奇偶校验错误
        case SerialPortEvent.CD: // 载波检测
        case SerialPortEvent.CTS: // 清除发送
        case SerialPortEvent.DSR: // 数据设备准备好
        case SerialPortEvent.RI: // 响铃侦测
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 输出缓冲区已清空
            break;
        case SerialPortEvent.DATA_AVAILABLE: // 有数据到达
            readComm();
            break;
        default:
            break;
        }
    }
 
    // 读取串口返回信息
    public void readComm() {
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
            System.out.println("receive data:"+test);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
   
     
    //向串口发送数据
    public void sendMsg(byte[] data){
        try {
            //实例化输出流
        	if(outputStream == null) 
        		outputStream = serialPort.getOutputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            outputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("send");
    }
    
    public  String byteToHex(byte b){  
        String hex = Integer.toHexString(b & 0xFF);  
        if(hex.length() < 2){  
            hex = "0" + hex;  
        }  
        return hex;  
    }
  /*  public byte[] hexStringTobytes(String hex) {
    	char[] charArray = hex.toCharArray();
    	List<Byte> bytes = new ArrayList();
    	for(int i= 0 ; i< charArray.length;i++,i++) {
    		if(charArray[i] == ' ') {
    			i++;
    		}
    		StringBuilder  sb = new StringBuilder();
    		sb.append(charArray[i]);
    		sb.append(charArray[i+1]);
    		String string = sb.toString();
    		byte one = (byte)Integer.parseInt(string, 16);
    		
    	}
    	
    	return null;
    }*/
    
    public void run() {
        init();
        byte[] data = new byte[] {(byte)0xFF,(byte)0x0A ,(byte)0x01 ,(byte)0x17 ,(byte)0x00,(byte)0x00 ,(byte)0x00,(byte)0x00 ,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00 ,(byte)0x00,(byte)0x0A ,(byte)0x00,(byte)0x00 ,(byte)0x00,(byte)0x00 ,(byte)0x00,(byte)0x00 ,(byte)0x00,(byte)0x00 ,(byte)0x00,(byte)0x00 ,(byte)0xFF};
        sendMsg(data);
    }
    public static void main(String[] args) {
		SerialPortTest1 instance = SerialPortTest1.getInstance("COM3");
//		instance.run();
	}
}