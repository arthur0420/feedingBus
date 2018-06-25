package arthur.feedingControl.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

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
}
