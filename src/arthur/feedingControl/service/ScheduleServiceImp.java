package arthur.feedingControl.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

public class ScheduleServiceImp extends BaseService implements ScheduleService {
	static Logger log = Logger.getLogger(ScheduleServiceImp.class);
	@Override
	public List<HashMap> getSchedule(int scheduleId) {
		
		Connection con= getConnection();
		if(con== null)return null;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sqlSchedule = "select * from schedule where id = "+scheduleId;
			ps = con.prepareStatement(sqlSchedule);
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
	public List<HashMap> getScheduleDay(int scheduleId) {

		Connection con= getConnection();
		if(con== null)return null;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sqlScheduleDay = "select * from schedule_day where schedule_id = "+scheduleId +" order by days";
			ps = con.prepareStatement(sqlScheduleDay);
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
	public List<HashMap> getScheduleHour(int scheduleId) {
		Connection con= getConnection();
		if(con== null)return null;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sqlScheduleHour = "select * from schedule_hour where schedule_id = "+scheduleId +" order by days";;
			
			ps = con.prepareStatement(sqlScheduleHour);
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
	public void modifyShceduleDay(int scheduleId,JSONArray dataList) {
		Connection con= getConnection();
		if(con== null)return ;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sqlScheduleHour = "delete from schedule_day where schedule_id =  "+scheduleId +" ";;
			ps = con.prepareStatement(sqlScheduleHour);
			ps.executeUpdate();
			String insertSql = "insert into schedule_day(schedule_id,days,fodder) values(?,?,?)";
			ps = con.prepareStatement(insertSql);
			for(int i = 0 ; i< dataList.size(); i++){
				JSONObject one = dataList.getJSONObject(i);
				int days = Integer.parseInt(one.getString("days")) ;
				int fodder = Integer.parseInt(one.getString("fodder")) ;
				ps.setInt(1, scheduleId);
				ps.setInt(2, days);
				ps.setInt(3, fodder);
				ps.addBatch();
			}
			ps.executeBatch();
			log.info("executeBatch");
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
	
	@Override
	public void modifyShcedule(int scheduleId, HashMap<String, Integer> param) {
		Connection con= getConnection();
		if(con== null)return ;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sql = "update schedule set id = id  ";
			Iterator<Entry<String, Integer>> iterator = param.entrySet().iterator();
			while(iterator.hasNext()){
				 Entry<String, Integer> next = iterator.next();
				 String key = next.getKey();
				 Integer value = next.getValue();
				 sql += " , "+key+"="+value;
			}
			
			sql += "  where id =  "+scheduleId +" ";
			log.info(sql);
			ps = con.prepareStatement(sql);
			ps.executeUpdate();
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

	@Override
	public void modifyShceduleHour(int scheduleId, JSONArray dataList) {
		Connection con= getConnection();
		if(con== null)return ;
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			String sqlScheduleHour = "delete from schedule_hour where schedule_id =  "+scheduleId +" ";;
			ps = con.prepareStatement(sqlScheduleHour);
			ps.executeUpdate();
			
			
			for(int i = 0 ; i< dataList.size(); i++){
				String insertSqlPre = " insert into schedule_hour(schedule_id ";
				String insertSqlBe = " values( "+scheduleId;
				JSONObject one = dataList.getJSONObject(i);
				Set<Entry> entrySet = one.entrySet(); 
				Iterator<Entry> iterator = entrySet.iterator();
				while(iterator.hasNext()){
					Entry next = iterator.next();
					String key = next.getKey()+"";
					String value = next.getValue()+"";
					insertSqlPre +=","+key;
					insertSqlBe +=","+value;
				}
				String insertSql = insertSqlPre + ")  "+insertSqlBe+")";
				log.info("insertSql :"+insertSql);
				ps.executeUpdate(insertSql);
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
}
