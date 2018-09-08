package arthur.feedingControl.utils;

import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import arthur.feedingControl.service.CellsService;
import arthur.feedingControl.service.CellsServiceImp;
import arthur.feedingControl.service.EventService;
import arthur.feedingControl.service.EventServiceImp;

public class DailyTask extends TimerTask {

	@Override
	public void run() {
		//每天凌晨更新常量
		Config.initConfig();
		
		//-特别关注。   验孕提醒,断奶，产仔提醒，喂食速度
//		String[] attentions = {"pregnancy_test","weaning_attention","farrow_attentiong"};
		
		
		EventService es = new EventServiceImp();
		List<HashMap> groupMax = es.getGroupMax();
		
		HashMap constant = Config.getConstant("pregnancy_test");
		String sw = constant.get("switch")+"";
		
		
		
	}

}
