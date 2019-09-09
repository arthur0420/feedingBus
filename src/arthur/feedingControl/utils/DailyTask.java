package arthur.feedingControl.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import arthur.feedingControl.service.CellsService;
import arthur.feedingControl.service.CellsServiceImp;
import arthur.feedingControl.service.EventService;
import arthur.feedingControl.service.EventServiceImp;
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;

public class DailyTask extends TimerTask {

	@Override
	public void run() {
		//每天凌晨更新常量
		Config.initConfig();
		
		//-特别关注。   验孕提醒,断奶，产仔提醒，喂食速度
//		String[] attentions = {"pregnancy_test","weaning_attention","farrow_attentiong"};
		
		List<HashMap> ptlist = new ArrayList<HashMap>();
		
		EventService es = new EventServiceImp();
		List<HashMap> groupMax = es.getGroupMax();
		
		
		
		HashMap ptc = Config.getConstant("pregnancy_test");
		int ptdays = (Integer )ptc.get("days");
		String ptsw = ptc.get("switch")+"";
		
		
		
		
		HashMap wac = Config.getConstant("weaning_attention");
		String wasw = wac.get("switch")+"";
		int wadays = (Integer)wac.get("days"); // 预计断奶,之前的天数。
		HashMap wc = Config.getConstant("weaning");
		int wdays = (Integer)wc.get("days");  // 产仔之后多少天预计断奶。
		int waLastEventDays = wdays - wadays;
		List<HashMap> walist = new ArrayList<HashMap>();
		
		
		
		HashMap fac = Config.getConstant("farrow_attentiong");
		HashMap gc = Config.getConstant("gestation");
		String fasw = fac.get("switch")+"";
		int fadays = (Integer)fac.get("days"); // 预计分娩前多少天，提醒
		int gdays = (Integer)gc.get("days");//受精 之后 多少天预计分娩
		int faLastEventDays = gdays -fadays;  //受精之后 多少天提醒，即将分娩。
		List<HashMap> falist = new ArrayList<HashMap>();
		
		
		try {
			for(int i = 0 ; i< groupMax.size(); i++){
				HashMap one = groupMax.get(i);
				Integer eventNo = (Integer)one.get("event_no");
				String eventDate = one.get("date")+"";
				if(ptsw.equals("1")){//验孕提醒开关       
					if(eventNo == 2){ //验孕提醒   最大的事件应该是受精 2
						int minus = DateFormat.minus(eventDate);
						if(minus ==ptdays){  // 配种 后 ptdays天，提醒
							ptlist.add(one);
						}
					}
				}
				
				if(wasw.equals("1")){// 断奶 提醒  开关
					if(eventNo == 6){ //断奶 提醒   最大的事件应该是产仔 6
						int minus = DateFormat.minus(eventDate);
						if(minus ==waLastEventDays ){
							walist.add(one);
						}
					}
				}
				
				if(fasw.equals("1")){// 产仔提醒
					if(eventNo == 5){ //验孕提醒   最大的事件应该是验孕怀孕 5
						String id =one.get("cell_id")+"";
						List<HashMap> event = es.getEvent(id);
						
						for(int j = 0 ; j<event.size();j++){ // desc
							HashMap hashMap = event.get(j);
							int innerEvent_no =  (Integer)hashMap.get("event_no");
							if(innerEvent_no == 2){
								eventDate = hashMap.get("date")+"";
							}
						}
						int minus = DateFormat.minus(eventDate);
						if(minus ==faLastEventDays ){
							falist.add(one);
						}
					}
				}
			}
			
			// 处理 log
//			ptlist  walist  falist
			LogService ls = new LogServiceImp();
			CellsService cs =  new  CellsServiceImp();
			for(int i = 0 ; i<ptlist.size(); i++){ // 验孕 提醒
				HashMap one= ptlist.get(i);
				Integer cell_id = (Integer)one.get("cell_id");
				HashMap cellLocation = cs.getCellLocationById(cell_id);
				
				String logText = "验孕提醒："+cellLocation.get("pl_name")+","+cellLocation.get("ap_name")+",栏位"+cellLocation.get("no_in_apartment");
				ls.AddLost("warning", logText);
			}
			for(int i = 0 ; i<walist.size(); i++){ // 验孕 提醒
				HashMap one= walist.get(i);
				Integer cell_id = (Integer)one.get("cell_id");
				HashMap cellLocation = cs.getCellLocationById(cell_id);
				
				String logText = "断奶提醒："+cellLocation.get("pl_name")+","+cellLocation.get("ap_name")+",栏位"+cellLocation.get("no_in_apartment");
				ls.AddLost("warning", logText);
			}
			for(int i = 0 ; i<falist.size(); i++){ // 验孕 提醒
				HashMap one= falist.get(i);
				Integer cell_id = (Integer)one.get("cell_id");
				HashMap cellLocation = cs.getCellLocationById(cell_id);
				
				String logText = "分娩提醒："+cellLocation.get("pl_name")+","+cellLocation.get("ap_name")+",栏位"+cellLocation.get("no_in_apartment");
				ls.AddLost("warning", logText);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		DailyTask dailyTask = new DailyTask();
		dailyTask.run();
	}

}
