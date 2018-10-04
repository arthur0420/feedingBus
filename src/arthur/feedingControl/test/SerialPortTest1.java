package arthur.feedingControl.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.TooManyListenersException;
 
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
 
public class SerialPortTest1 implements Runnable, SerialPortEventListener {
	private static String comName = "COM4"; // 配置文件
	
    // 检测系统中可用的通讯端口类
    private CommPortIdentifier portId;
    // 枚举类型
    private Enumeration<CommPortIdentifier> portList;
 
    // RS232串口
    private SerialPort serialPort;
    
    // 输入输出流
    private InputStream inputStream;
    private OutputStream outputStream;
 
    // 保存串口返回信息
 
    // 单例创建
    private static SerialPortTest1 uniqueInstance = new SerialPortTest1();
 
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
        byte[] readBuffer = new byte[1024];
        try {
            inputStream = serialPort.getInputStream();
            // 从线路上读取数据流
            int len = 0;
            String test ="";
            
            
            while ((len = inputStream.read(readBuffer)) != -1) {
                System.out.println("实时反馈：" + new String(readBuffer, 0, len).trim() + new Date());
                test += new String(readBuffer, 0, len).trim();
                break;
            }
            System.out.println(test + " ");
            //closeSerialPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    public void closeSerialPort() {
        uniqueInstance.serialPort.close();
    }
     
    //向串口发送数据
    public void sendMsg(){
        String information = "AT\r";
        try {
            //实例化输出流
            outputStream = serialPort.getOutputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {
            outputStream.write(information.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("send");
    }
 
    @Override
    public void run() {
        init();
        sendMsg();
         
    }
}