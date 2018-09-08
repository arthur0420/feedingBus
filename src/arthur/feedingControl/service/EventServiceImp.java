package arthur.feedingControl.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

public class EventServiceImp extends BaseService implements EventService {

	@Override
	public List<HashMap> getEvent(String id) {
		Connection con = getConnection();
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			if(con == null )return null;
			String psql = "select * from  event where cell_id = "+id +" order by date desc";
			ps= con.prepareStatement(psql);
			r = ps.executeQuery();
			List<HashMap> list = getList(r);
			return list;
		} catch (Exception e) {
			log.error("error",e);
			return null;
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
	}

	@Override
	public void addEvent(HashMap param) {
		Connection con = getConnection();
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			if(con == null )return ;
			String psql = "insert into event(event_no,`date`,boar,executor,result,cell_id) values(?,?,?,?,?,?)";
			ps= con.prepareStatement(psql);
			
			String event_no =  (String)param.get("event_no");
			ps.setString(1,event_no);
			
			String date = (String)param.get("date");
			ps.setString(2, date);
			
			if(param.containsKey("boar")){
				String boar = (String)param.get("boar");
				ps.setString(3, boar);
			}else{
				ps.setNull(3,java.sql.Types.VARCHAR);
			}
			
			if(param.containsKey("executor")){
				String executor = (String)param.get("executor");
				ps.setString(4, executor);
			}else{
				ps.setNull(4,java.sql.Types.VARCHAR);
			}
			
			if(param.containsKey("result")){
				String result = (String)param.get("result");
				ps.setString(5, result);
			}else{
				ps.setNull(5,java.sql.Types.VARCHAR);
			}
			
			String cell_id = param.get("cell_id")+"";
			ps.setString(6, cell_id);
			
			
			int executeUpdate = ps.executeUpdate();
			log.info("cell_id:"+cell_id+",executeupdate:"+executeUpdate);
			return ;
		} catch (Exception e) {
			log.error("error",e);
			return ;
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
	}

	@Override
	public List<HashMap> getGroupMax() {
		Connection con = getConnection();
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			if(con == null )return null;
			String psql = "select cell_id,max(event_no) event_no,max(date) date from event group by cell_id";
			ps= con.prepareStatement(psql);
			r = ps.executeQuery();
			List<HashMap> list = getList(r);
			return list;
		} catch (Exception e) {
			log.error("error",e);
			return null;
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
	}
	
}
