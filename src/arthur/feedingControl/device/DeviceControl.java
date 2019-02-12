package arthur.feedingControl.device;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.xml.internal.ws.wsdl.writer.document.Port;

import arthur.feedingControl.service.BaseService;
import arthur.feedingControl.utils.Config;
import arthur.feedingControl.utils.SerialPortHandler;
import sun.print.resources.serviceui;




// 先写 127.0.0.0的。  再实现，对ip的通讯

public class DeviceControl extends BaseService{
	
	private static Logger log = Logger.getLogger(DeviceControl.class);
	private static SerialPortHandler[] serialPorts = new SerialPortHandler[2]; // 串口对象
	private static DeviceControl single = null;
	private static Object lock = new Object();
	private byte[] receiveData = null;
	public  static  void notifyThis(byte[] receiveData) {
		synchronized (lock) {
			receiveData = null;
			lock.notifyAll();
		}
	}
	private DeviceControl() {}
	public static   DeviceControl getInstance() {
		if(single == null) {
			single = new DeviceControl();
			single.init();
			return single;
		}else {
			return null;
		}
	}
	public static void close() {
		single = null;
	}
	public void init() {
		if(serialPorts[0] == null) {
			String spName1 = Config.getBcConfig("com1");
			String spName2 = Config.getBcConfig("com2");
			serialPorts[0] = SerialPortHandler.getInstance(spName1);
			serialPorts[1] = SerialPortHandler.getInstance(spName2);
		}
	}
	
	public  void sendSingleTask(String portId,int time) {
		Connection con = getConnection();
		if(con== null)return ;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sql = "select * from device where id = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, portId);
			r = ps.executeQuery();
			List<HashMap> list = getList(r);
			HashMap hashMap = list.get(0);
			String ip = (String)hashMap.get("ip"); // ip
			String sp = (String)hashMap.get("sp");  //串口号 1,2
			String rs = (String)hashMap.get("rs"); 	// 串口地址码 1-10
			String s = (String)hashMap.get("switch");// 开关码 1-10
			
			if(ip.equals("127.0.0.1")) {
				int spint = Integer.parseInt(sp);
				int rsint = Integer.parseInt(rs);
				int sint = Integer.parseInt(s);
				byte[] data = new byte[] {(byte)0xFF,(byte)0x00 ,(byte)0x01 ,(byte)0x17 ,
						(byte)0x00,(byte)0x00 ,
						(byte)0x00,(byte)0x00 ,
						(byte)0x00,(byte)0x00 ,
						(byte)0x00,(byte)0x00 ,
						(byte)0x00,(byte)0x00 ,
						(byte)0x00,(byte)0x00 ,
						(byte)0x00,(byte)0x00 ,
						(byte)0x00,(byte)0x00 ,
						(byte)0x00,(byte)0x00 ,
						(byte)0x00,(byte)0x00 ,
						(byte)0xFF};
				data[1] = (byte)rsint;
				data[3+sint*2] = (byte)60;
				SerialPortHandler sph = serialPorts[spint-1];
				sph.sendMsg(data);
				
				synchronized (lock) {
					lock.wait();
				}
			}else {
				//TODO  to other 
			}
			
			return ;
		} catch (Exception e) {
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return ;
	}
	public void sendBatchTask() {
		
	}
}
