package arthur.feedingControl.device;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.sun.jmx.snmp.tasks.TaskServer;
import com.sun.org.apache.bcel.internal.generic.INEG;
import com.sun.xml.internal.ws.wsdl.writer.document.Port;

import arthur.feedingControl.entity.Task;
import arthur.feedingControl.service.BaseService;
import arthur.feedingControl.service.CellsService;
import arthur.feedingControl.service.CellsServiceImp;
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;
import arthur.feedingControl.utils.Config;
import arthur.feedingControl.utils.SerialPortHandler;
import sun.print.resources.serviceui;




// 先写 127.0.0.0的。  再实现，对ip的通讯

public class DeviceControl extends BaseService{
	
	private static Logger log = Logger.getLogger(DeviceControl.class);
	private static SerialPortHandler[] serialPorts = new SerialPortHandler[2]; // 串口对象
	private static DeviceControl single = null;
	private static Object lock = new Object(); // 也是锁对象，也是wait对象。
	private byte[] receiveData = null;
	public  static  void notifyThis(byte[] receiveData) {
		synchronized (lock){
			single.receiveData = receiveData;
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
	
	// 测试 测速用。
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
			String s = (String)hashMap.get("switch");// 开关码 1-10  sql别名
			
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
			e.printStackTrace();
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return ;
	}
	// 连通性测试专用，多设备。
	// 默认  ip为本机  串口号为1， 地址码为1。多设备是开关号。
	public  void sendSingleTask(String[] devices,byte time) {
		Connection con = getConnection();
		if(con== null)return ;
		PreparedStatement ps=null;
		ResultSet r = null;
		
		try {
			byte[] data = new byte[] {(byte)0xFF,(byte)0x01 ,(byte)0x01 ,(byte)0x17 ,
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
			
			for(int i = 0 ; i<devices.length ;i++){
				String portId =devices[i];
				if(portId == null || "".equals(portId)) continue;
				String sql = "select * from device where id = ?";
				ps = con.prepareStatement(sql);
				ps.setString(1, portId);
				r = ps.executeQuery();
				List<HashMap> list = getList(r);
				if(list.size() == 0)continue;
				HashMap hashMap = list.get(0);
				String s = (String)hashMap.get("switch");// 开关码 1-10  sql别名
				
				int sint = Integer.parseInt(s);
//				data[1] = (byte)rsint;
				data[3+sint*2] = (byte)time;
			}
			SerialPortHandler sph = serialPorts[0];
			sph.sendMsg(data);
			synchronized (lock) {
				lock.wait();
			}
			return ;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return ;
	}
	//批量任务，正常系统执行用。
	public void sendBatchTask(List<HashMap> feed) {
		String minstr = Config.getBcConfig("min");
		String maxstr = Config.getBcConfig("max");
		int min = Integer.parseInt(minstr);
		int max = Integer.parseInt(maxstr);
		
		HashMap calibration = Config.getConstant("calibration");
		int speed = (int)calibration.get("days"); // 每分钟的下料多少克。
		double perSecond = speed/60.0; // 每秒
		HashMap<String,Task> keyMap = new HashMap(); 
		for(int i = 0 ; i<feed.size();i++) {  // 先把每一台电机控制板的数据组装好。
			HashMap hashMap = feed.get(i);
			int wfwAS =  (int)hashMap.get("wfwac");
			String ip = (String)hashMap.get("ip"); // ip
			String sp = (String)hashMap.get("sp");  //串口号 1,2
			String rs = (String)hashMap.get("rs"); 	// 串口地址码 1-10
			String s = (String)hashMap.get("ds");// 开关码 1-10
			int spint = Integer.parseInt(sp);
			int rsint = Integer.parseInt(rs);
			int sint = Integer.parseInt(s);
			
			String key = ip+sp+rs; // 一台电机控制板
			int willRunSeconds = (int) (wfwAS / perSecond) ;
			byte h =(byte)(willRunSeconds>> 8 &0xff );
			byte l =(byte)(willRunSeconds &0xff);
			Task task = null;
			if(keyMap.containsKey(key)) {
				task = keyMap.get(key);
			}else {
				task = new Task();
				keyMap.put(key, task);
				task.ip = ip;
				task.sp = spint;
				task.rs = rsint;
				byte[] data = new byte[] {(byte)0xFF,(byte)rsint ,(byte)0x01 ,(byte)0x17 ,
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
				HashMap[] celldata = new HashMap[10];
				task.data = data;
				task.celldata = celldata;
			}
			
			byte[] data  = task.data;
			HashMap[] celldata = task.celldata;
			int hi = 3+sint*2 -1;
			int li = 3+sint*2;
			data[hi] = h;
			data[li] = l;
			celldata[sint-1] = hashMap;
		}
		HashMap<String, List<Task>> ipList = new HashMap();
		Iterator<Task> iterator = keyMap.values().iterator();
		while(iterator.hasNext()) { // 根据ip  分组。
			Task next = iterator.next();
			String ip = next.ip;
			if(ipList.containsKey(ip)) {
				List<Task> list = ipList.get(ip);
				list.add(next);
			}else {
				List<Task>  list = new ArrayList();
				list.add(next);
				ipList.put(ip, list);
			}
		}
		
		Iterator<Entry<String, List<Task>>> iterator2 = ipList.entrySet().iterator();
		while(iterator2.hasNext()) {
			Entry<String, List<Task>> next = iterator2.next();
			List<Task> value = next.getValue();
			String targetIp = next.getKey();
			if(targetIp.equals("127.0.0.1")) {
				for(int i = 0 ; i<  value.size();i++) { //  本机下  有多少个task要发送。  挨个发送。
					Task t = value.get(i);
					SerialPortHandler sph = serialPorts[t.sp-1];
					sph.sendMsg(t.data);
					synchronized (lock) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(receiveData==null) continue;
					for(int j = 0 ; j<10;j++) {
						int mv = (receiveData[4+j*2]&0xff) << 8 | (receiveData[5+j*2]&0xff) ;
						if(t.data[j] == 0)continue;
						if(mv<min) {
							error(t.celldata[j],'l');
						}else if( mv > max) {
							error(t.celldata[j],'h');
						}
					}
				}
			}else {
				//TODO  to other 非本机的代码还没有写
			}
		}
	}
	
	private void error(HashMap cells,char hl) {
		if(cells == null)return ;
		String no_in_apartment =  cells.get("no_in_apartment")+"";
		String pl_name = cells.get("pl_name")+"";
		String ap_name = cells.get("ap_name")+"";
		LogService ls = new LogServiceImp();
		String logtext = pl_name+"-"+ap_name+"-"+no_in_apartment+"栏位喂食错误：";
		if(hl == 'h') {
			logtext +="堵转";
		}else {
			logtext +="空转或断路";
		}
		ls.AddLost("error",logtext );
	}
}
