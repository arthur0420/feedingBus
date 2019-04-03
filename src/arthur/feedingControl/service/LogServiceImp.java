package arthur.feedingControl.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import arthur.feedingControl.utils.DateFormat;
import sun.security.action.GetLongAction;

public class LogServiceImp extends BaseService implements LogService {
	static Logger log = Logger.getLogger(LogServiceImp.class);
	@Override
	public List<HashMap> getLogs(int pageIndex, int pageSize,
			String dateStart, String dateEnd, String level)  {
		
		Connection con = getConnection();
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			if(con == null )return null;
			int start = (pageIndex -1)* pageSize;
			String psql = "select * from log where 1=1 ";
			if(level!=null && !"".equals(level)) psql = psql + " & level='"+level+"'";
			psql = psql + " order by date desc ";
			psql = psql + "   limit "+start+","+pageSize;
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
	public static void main(String[] args) {
		LogServiceImp logServiceImp = new LogServiceImp();
		List<HashMap> logs = logServiceImp.getLogs(3, 10, null, null, "error");
		System.out.println(logs.size());
	}
	@Override
	public void AddLost(String level, String logText) {
		Connection con = getConnection();
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			if(con == null )return ;
			String sql = "insert into log(level,remark,date) VALUES(?,?,?)";
			ps= con.prepareStatement(sql);
			ps.setString(1, level);
			ps.setString(2, logText);
			String nowdate = DateFormat.toString(new Date());
			ps.setString(3,nowdate);
			int executeUpdate = ps.executeUpdate();
			log.info("log. insert into execute result :"+executeUpdate);
		} catch (Exception e) {
			log.error("error",e);
		}finally{
			try { if(r!=null)r.close();} catch (Exception e2) {}
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
	}
}
