package arthur.feedingControl.main;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import arthur.feedingControl.device.DeviceControl;
import arthur.feedingControl.service.CellsService;
import arthur.feedingControl.service.CellsServiceImp;
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;
import arthur.feedingControl.utils.Config;
import arthur.feedingControl.utils.DailyTask;

public class LaunchProcessor extends Thread{
	private static Logger log = Logger.getLogger(LaunchProcessor.class);
	@Override
	public void run() {
		//初始化
		Config.initConfig();
		
		
		Calendar first = Calendar.getInstance();
		first.set(Calendar.DAY_OF_MONTH, first.get(Calendar.DAY_OF_MONTH)+1);
		first.set(Calendar.HOUR_OF_DAY, 0);
		first.set(Calendar.MINUTE, 0);
		first.set(Calendar.SECOND, 0);
		first.set(Calendar.MILLISECOND, 0);
		//-----------------------定时任务的部分
		Timer t1 = new Timer();// 每天更新config
		t1.scheduleAtFixedRate(new DailyTask(),first.getTime() , 24*60*60*1000l);
		
		
		Calendar cal = Calendar.getInstance();
		int mi = cal.get(Calendar.MINUTE);
		long delay = 0;
		delay = (60 - mi)* 60 * 1000 + 100 ;  // 放置
		Timer t2 = new Timer();
		t2.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				CellsService cs = new CellsServiceImp();
				List<HashMap> feed = cs.toFeed();
				
				LogService ls = new LogServiceImp();
				ls.addRecord(feed);
				
				DeviceControl instance = DeviceControl.getInstance();
				int i = 0 ; 
				while(instance == null) {
					i++;
					if(i == 10) {
						log.error("getinsatance timeout");
						return;
					}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					instance = DeviceControl.getInstance();
				}
				instance.sendBatchTask(feed);
				DeviceControl.close();
			}	
		}, delay, 60 *60 * 1000);
	}
}
