package arthur.feedingControl.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import arthur.feedingControl.utils.DateFormat;
import arthur.feedingControl.utils.Config;

public class CellsServiceImp extends BaseService implements CellsService {
	static Logger log = Logger.getLogger(CellsServiceImp.class);
	private HashMap<Integer ,HashMap> scheduleCache = new HashMap<Integer,HashMap>();
	private HashMap<Integer ,List<HashMap>> scheduleDayCache = new HashMap<Integer ,List<HashMap>>();
	private HashMap<Integer ,List<HashMap>> scheduleHourCache = new HashMap<Integer ,List<HashMap>>();
	
	@Override
	public List<HashMap> getCells(String apartmentId, String noInApartmentP) {
		
		Connection con = getConnection();
		if(con== null)return null;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sql = "select * from cells where 1=1 ";
			if(apartmentId!=null){
				sql = sql +" and apartment_id = "+apartmentId;
			}
			if(noInApartmentP !=null ){
				sql = sql +" and no_in_apartment = "+noInApartmentP;
			}
			ps = con.prepareStatement(sql);
			r = ps.executeQuery();
			List<HashMap> list = getList(r);
			
			for(int i = 0 ; i <list.size() ; i++){
				HashMap one = list.get(i);
				int haveAnimal = (Integer)one.get("have_animal");
				int switchI = (Integer)one.get("switch");
				if(haveAnimal != 1 || switchI !=1){
					continue;
				}
				String cellId = ""+one.get("id");
				int rankNum = getRankNum(cellId); // 天数
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
			return list;
		} catch (Exception e) {
			log.error("",e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return null;
	}
	
	@Override
	public List<HashMap> getCellsEvents( String cellId) {
		Connection con = getConnection();
		if(con== null)return null;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sql = "select * from event where 1=1 ";
			sql = sql +" and cell_id = "+cellId;
			sql = sql +" order by event_no desc ";
			ps = con.prepareStatement(sql);
			r = ps.executeQuery();
			List<HashMap> list = getList(r);
			return list;
		} catch (Exception e) {
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return null;
	}
	
	@Override
	public HashMap getCellsStatistic(String apartmentId) {
		List<HashMap> cells = getCells(apartmentId, null);
		
		HashMap data = new HashMap();
		int animalGross = 0; // have animal =1   +1
		int deliveryGross = 0; // days >0 +1
		int addGross = 0 ; // offset > 0; +1
		int minus = 0 ; // offset <0; +1
		int unableFeed = 0; // switch = 0 ; +1 
		int weightGross = 0; //  += wfwac
		int fedGross = 0; // += wfwac* fedPercent;
		for(int i = 0 ; i< cells.size(); i++){
			HashMap oneCell = cells.get(i);
			int haveAnimal = (Integer)oneCell.get("have_animal");
			int switchI = (Integer)oneCell.get("switch");
			if(haveAnimal != 1 || switchI !=1){
				continue;
			}
			int have_animal = (Integer)oneCell.get("have_animal");
			int days = (Integer)oneCell.get("days");
			int offset = (Integer)oneCell.get("offset");
			int iSwitch = (Integer) oneCell.get("switch");
			int wfwac = (Integer) oneCell.get("wfwac");
			int percent = (Integer)oneCell.get("fedPercent");
			
			
			if(have_animal==1){
				animalGross++;
			}
			if(days > 0 ){
				deliveryGross ++;
			}
			if(offset >0){
				addGross++;
			}else if(offset <0){
				minus++;
			}
			if(iSwitch == 0 ){
				unableFeed ++;
			}
			weightGross+= wfwac;
			fedGross += wfwac * percent /100;
		}
		data.put("animalGross", animalGross);
		data.put("deliveryGross",deliveryGross);
		data.put("addGross", addGross);
		data.put("minus", minus);
		data.put("unableFeed",unableFeed );
		data.put("weightGross", weightGross);
		data.put("fedGross", fedGross);
		return data;
	}
	
	@Override
	public void initCell(int id,String scheduleId) {
		Connection con = getConnection();
		if(con== null) {
			log.error("获取连接失败");
			return;
		}
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sql = "update cells set  ";
			sql += " offset = 0" ;
			sql += " skip_time = 0" ;
			sql += " have_animal = 1";
			sql += ", `switch` = 1";
			sql += ", feeding_schedule = "+scheduleId;
			sql += " where id = "+id;
			log.info("logsql :"+sql );
			ps = con.prepareStatement(sql);
			int executeUpdate = ps.executeUpdate();
			log.info("init count :"+executeUpdate);
		} catch (Exception e) {
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return ;
	}
	@Override
	public void modifyCell(int id ,int skip_time ,int offset ,int switchh) {
		Connection con = getConnection();
		if(con== null) {
			log.error("获取连接失败");
			return;
		}
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sql = "update cells set  ";
			sql += " skip_time = "+skip_time ;
			sql += ", offset = "+offset ;
			sql += ", `switch` = "+switchh ;
			sql += " where id = "+id;
			log.info("logsql :"+sql );
			ps = con.prepareStatement(sql);
			int executeUpdate = ps.executeUpdate();
			log.info("modify count :"+executeUpdate);
		} catch (Exception e) {
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return ;
	}
	
	
	/*
	 * 已经喂食的比例。当日。百分比。
	 */
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
	private int getWillFeedWeightOnce(int days,List<HashMap> scheduleHour) {
		
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
		
		int hPercent = (Integer)hh.get("h"+nowHour);
		return hPercent;
		
		
	}
	/**
	 * 获取天数
	 * 根据 受精时间 和 产仔时间来确定天数。
	 * @return
	 * @throws ParseException 
	 */
	private int getRankNum(String cellId) {
		
		List<HashMap> cellsEvents = getCellsEvents(cellId);
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
					HashMap constant = Config.getConstant("gestation");
					int days = (Integer)constant.get("days");
					int ct =  minus - days;
					if(ct>0)ct=0;
					return ct;
				}
			} catch (Exception e) {
				log.error("",e);
			}
		}
		log.error("没有受精事件，没有分娩事件。不能确定天数");
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

	@Override
	public void clearCell(int id) {
		Connection con = getConnection();
		if(con== null) {
			log.error("获取连接失败");
			return;
		}
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sql = "update cells set  ";
			sql += " skip_time = "+0 ;
			sql += ", offset = "+0;
			sql += ", `switch` = "+0;
			sql += ", have_animal = "+0;
			sql += " where id = "+id;
			log.info("logsql :"+sql );
			ps = con.prepareStatement(sql);
			int executeUpdate = ps.executeUpdate();
			
			String deleteSql = "delete from event where cell_id ="+id;
			int executeUpdate2 = ps.executeUpdate(deleteSql);
			
			log.info("clear cell :"+executeUpdate);
			log.info("clear event :"+executeUpdate2);
		} catch (Exception e) {
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return ;
	}
	
	@Override
	public HashMap getCellLocationById(int id) {
		Connection con = getConnection();
		if(con== null)return null;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sql = "select c.no_in_apartment,a.* from cells c left join apartment a on c.apartment_id = a.id  where c.id =  "+id;
			ps = con.prepareStatement(sql);
			r = ps.executeQuery();
			List<HashMap> list = getList(r);
			HashMap hashMap = list.get(0);
			return hashMap;
		} catch (Exception e) {
			log.error(e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return null;
		
	}

	@Override
	public List<HashMap> toFeed() {
		Connection con = getConnection();
		if(con== null)return null;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			List<Integer > willUpdateSkipTime = new ArrayList();
			String sql = " select c.*,d.ip,d.sp,d.rs,d.switch ds,a.pl_name,a.ap_name from cells c "
					+ " left join device d  on c.deviceid = d.id "
					+ " left join apartment a on a.id = c.apartment_id";
			
			ps = con.prepareStatement(sql);
			r = ps.executeQuery();
			List<HashMap> list = getList(r);
			List<HashMap> rl = new ArrayList();
			for(int i = 0 ; i <list.size() ; i++){
				HashMap one = list.get(i);
				int id = (int)one.get("id");
				int haveAnimal = (Integer)one.get("have_animal");
				int switchI = (Integer)one.get("switch");
				if(haveAnimal != 1 || switchI !=1){
					continue;
				}
				int skipTime = (Integer)one.get("skip_time");
				String cellId = ""+one.get("id");
				int rankNum = getRankNum(cellId); // 天数
				one.put("days", rankNum);
				
				int scheduleId =  (Integer)one.get("feeding_schedule");
				
				HashMap schedule = getSchedule(scheduleId);
				List<HashMap> scheduleDay = getScheduleDay(scheduleId);
				List<HashMap> scheduleHour = getScheduleHour(scheduleId);
				
				one.put("schedule_name", schedule.get("name"));// 饲喂计划名称
				
				int willFeedWeightOnce = getWillFeedWeight(rankNum, scheduleDay);  // 当日喂食重量
				int percentDay = getWillFeedWeightOnce(rankNum, scheduleHour);// 当前时间，喂食比例 当日比例
				if(skipTime!=0 && willFeedWeightOnce !=0 && percentDay !=0  ) { // 跳过次数不等于0  需要跳过。  且有东西 需要喂。
					willUpdateSkipTime.add(id);
					continue;
				}
				if(willFeedWeightOnce == 0 || percentDay == 0 )continue; // 不需要喂
				
				
				int wfwAS = 0; // 根据饲喂计划的offset 修正数量  只存在一个。
				int offsetRelative = (Integer)schedule.get("offset_relative"); 
				int offsetAbsolute= (Integer)schedule.get("offset_absolute");
				if(offsetAbsolute != 0 ){
					wfwAS = willFeedWeightOnce + offsetAbsolute;
				}else if(offsetRelative !=0 ){
					wfwAS = willFeedWeightOnce + offsetRelative * willFeedWeightOnce / 100;  // 值可能会有问题
				}else{
					wfwAS = willFeedWeightOnce;
				}
				one.put("wfwbc", wfwAS);// 经过 饲喂计划修正 未根据栏位修正
				int offsetCell = (Integer)one.get("offset");
				if(offsetCell != 0 ){
					wfwAS = wfwAS + wfwAS * offsetCell / 100;
				}
				wfwAS = wfwAS * percentDay ;  // 不除以 100， 现在单位是g
				one.put("wfwac", wfwAS);// 完全修正之后。
				rl.add(one);
			}
			r.close();
			ps.close();
			r = null;
			String updateSql = "update cells set skip_time = skip_time -1 where id = ? and skip_time !=0 and skip_time is not null";
			ps = con.prepareStatement(updateSql);
			int size = willUpdateSkipTime.size();
			for(int i = 0 ;i<size ;i++) {
				int one = willUpdateSkipTime.get(i);
				ps.setInt(1, one);
				ps.addBatch();
				if(i% 10 == 0  || i ==(size-1)  ) { // 逢10或最后一条
					ps.executeBatch();
				}
			}
			return rl;
		}catch (Exception e) {
			log.error("---",e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
		return null;
	}
}
