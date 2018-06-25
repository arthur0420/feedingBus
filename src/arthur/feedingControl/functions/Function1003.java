package arthur.feedingControl.functions;

import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import arthur.feedingControl.entity.Result;
import arthur.feedingControl.service.CellsService;
import arthur.feedingControl.service.CellsServiceImp;
import arthur.feedingControl.service.ScheduleService;
import arthur.feedingControl.service.ScheduleServiceImp;
import arthur.feedingControl.utils.DateFormat;
import arthur.feedingControl.utils.config;

/**
 * 
 * @author arthu
 * 动物列表信息
 */

public class Function1003 extends BaseFunction{
	static Logger log = Logger.getLogger(Function1003.class);
	private CellsService  cs = null;
	private HashMap<Integer ,HashMap> scheduleCache = new HashMap<Integer,HashMap>();
	private HashMap<Integer ,List<HashMap>> scheduleDayCache = new HashMap<Integer ,List<HashMap>>();
	private HashMap<Integer ,List<HashMap>> scheduleHourCache = new HashMap<Integer ,List<HashMap>>();
	
	@Override
	public Result execute() {
		
		String apartmentId = getStrParameter("apartmentId");
		cs= new CellsServiceImp();
		List<HashMap> cells = cs.getCells(apartmentId, null);
		for(int i = 0 ; i <cells.size() ; i++){
			HashMap one = cells.get(i);
			String noInApartment = ""+one.get("no_in_apartment");
			int rankNum = getRankNum(apartmentId, noInApartment); // 天数
			one.put("days", rankNum);
			
			int scheduleId =  (Integer)one.get("feeding_schedule");
			
			HashMap schedule = getSchedule(scheduleId);
			List<HashMap> scheduleDay = getScheduleDay(scheduleId);
			List<HashMap> scheduleHour = getScheduleHour(scheduleId);
			
			one.put("schedule_name", schedule.get("name"));// 饲喂计划名称
			
			int willFeedWeight = getWillFeedWeight(rankNum, scheduleDay);
			log.info("will"+willFeedWeight);
			
			int wfwAS = 0; // 根据饲喂计划的offset 修正数量  只存在一个。
			int offsetRelative = (Integer)schedule.get("offset_relative");
			int offsetAbsolute= (Integer)schedule.get("offset_absolute");
			if(offsetAbsolute != 0 ){
				wfwAS = willFeedWeight + offsetAbsolute;
			}else if(offsetRelative !=0 ){
				wfwAS = willFeedWeight + offsetRelative * willFeedWeight / 100;  // 值可能会有问题
			}else{
				wfwAS = willFeedWeight;
			}
			one.put("wfwbc", wfwAS);// 经过 饲喂计划修正 未根据栏位修正
			int offsetCell = (Integer)one.get("offset");
			if(offsetCell != 0 ){
				wfwAS = wfwAS + wfwAS * offsetCell / 100;
			}
			
			one.put("wfwac", wfwAS);// 完全修正之后。
			int fedPercent = getFedPercent(rankNum, scheduleHour);
			one.put("fedPercent", fedPercent); // 已饲喂百分比
		}
		
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		result.setData(cells);
		return result;
	}
	
	private int getFedPercent( int days,List<HashMap> scheduleHour){
		
		HashMap hh = null;
		for(int i = 0 ; i <scheduleHour.size(); i++ ){ // 根据天数 或者 计划中 这天 应该饲喂的总量
			HashMap sh= scheduleHour.get(i);
			int sd = (Integer)sh.get("days");// 当前节点的天数
			
			if(i == 0 ){ // 计划里一个节点
				if(days <=  sd){ // 计划里第一个节点之前
					hh = sh;
					break;
				}
				continue;
			}
			if(i == (scheduleHour.size()-1)){ // 最后一个节点  必然  days 大于最后一个节点的天数
				hh = sh;
				break;
			}
			//去头去尾之后的节点
			if(days<=sd){
				hh = sh;
				break;
			}
		}
		Calendar cl = Calendar.getInstance();
		int nowHour = cl.get(Calendar.HOUR_OF_DAY);
		int percent = 0;
		for(int i = 0 ; i<24 ; i++){
			
			int hPercent = (Integer)hh.get("h"+i);
			if(i<= nowHour) percent+=hPercent;
		}
		return percent;
	}
	
	/**
	 * 当日将要喂多少 
	 * 单位是 0.1kg
	 * @param days
	 * @param scheduleDay
	 * @param scheduleHour
	 * @return
	 */
	private int getWillFeedWeight(int days,List<HashMap> scheduleDay){
		int fodder = 0;
		HashMap temp = null;
		for(int i = 0 ; i <scheduleDay.size(); i++ ){ // 根据天数 或者 计划中 这天 应该饲喂的总量
			HashMap sd= scheduleDay.get(i);
			int od = (Integer)sd.get("days");// 当前节点的天数
			if(i == 0 ){ // 计划里一个节点
				if(days <=  od){ // 计划里第一个节点之前
					fodder= (Integer)sd.get("fodder");
					break;
				}
				temp = sd;
				continue;
			}
			if(i == (scheduleDay.size()-1)){ // 最后一个节点  必然  days 大于最后一个节点的天数
				fodder = (Integer)sd.get("fodder");
				break;
			}
			//去头去尾之后的节点
			if(days<=od){
				int tf = (Integer)temp.get("fodder");
				int nf = (Integer)sd.get("fodder");
				
				int td = (Integer)temp.get("days");
				
				double rate = (days-td)*1.0 / (od -td);
				Double fodderTemp = rate * (nf- tf) +tf;
				fodder = fodderTemp.intValue();
				break;
			}else{
				temp =sd ;
			}
		}
		return fodder;
	}
	/**
	 * 获取天数
	 * 根据 受精时间 和 产仔时间来确定天数。
	 * @return
	 * @throws ParseException 
	 */
	private int getRankNum(String apartmentId ,String noInApartment) {
		
		List<HashMap> cellsEvents = cs.getCellsEvents(apartmentId, noInApartment);
		for(int i = 0 ; i< cellsEvents.size(); i++){
			HashMap event = cellsEvents.get(i);
			int eventNo = (Integer)event.get("event_no");
			String eventDate = (String)event.get("date");
			try {
				if(eventNo == 6){
					int minus = DateFormat.minus(eventDate);
					return minus;
				}else if(eventNo == 2){
					int minus = DateFormat.minus(eventDate);
					HashMap constant = config.getConstant("gestation");
					int days = (Integer)constant.get("days");
					int ct =  minus - days;
					if(ct>0)ct=0;
					return ct;
				}else{
					log.error("没有受精事件，没有分娩事件。不能确定天数");
				}
			} catch (Exception e) {
				log.error(e);
			}
		}
		return 0;
	}
	
	/*
	 * 获取 饲养计划的基本信息
	 */
	private HashMap getSchedule(int scheduleId){
		if(scheduleCache.containsKey(scheduleId)){
			return scheduleCache.get(scheduleId);
		}
		ScheduleService ss = new ScheduleServiceImp();
		List<HashMap> scheduleList = ss.getSchedule(scheduleId);
		if(scheduleList.size()!=0){
			HashMap hashMap = scheduleList.get(0);
			scheduleCache.put(scheduleId, hashMap);
			return hashMap;
		}
		else return  null;
	}
	private List<HashMap> getScheduleDay(int scheduleId){
		if(scheduleDayCache.containsKey(scheduleId)){
			return scheduleDayCache.get(scheduleId);
		}
		ScheduleService ss = new ScheduleServiceImp();
		List<HashMap> scheduleList = ss.getScheduleDay(scheduleId);
		if(scheduleList.size()!=0){
			scheduleDayCache.put(scheduleId, scheduleList);
			return scheduleList;
		}
		else return  null;
	}
	private List<HashMap> getScheduleHour(int scheduleId){
		if(scheduleHourCache.containsKey(scheduleId)){
			return scheduleHourCache.get(scheduleId);
		}
		ScheduleService ss = new ScheduleServiceImp();
		List<HashMap> scheduleList = ss.getScheduleHour(scheduleId);
		if(scheduleList.size()!=0){
			scheduleHourCache.put(scheduleId, scheduleList);
			return scheduleList;
		}
		else return  null;
	}
	
	
}
