package arthur.feedingControl.main;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import arthur.feedingControl.utils.Config;
import arthur.feedingControl.utils.DailyTask;

public class LaunchProcessor extends Thread{
	
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
		t1.scheduleAtFixedRate(new DailyTask(),first.getTime() , 24*60*60*1000);
		
		
		Calendar cal = Calendar.getInstance();
//		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int mi = cal.get(Calendar.MINUTE);
		long delay = 0;
		delay = (60 - mi)*60 * 1000 ;
		Timer t2 = new Timer();
		t2.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				
			}	
		}, delay, 60 *60 * 1000);
	}
}
