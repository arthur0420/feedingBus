package arthur.feedingControl.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

public class ConstantsServiceImp extends BaseService implements
		ConstantsService {

	@Override
	public List<HashMap> getConstants() {
		Connection con = getConnection();
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			if(con == null )return null;
			String psql = "select * from  constants";
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
	public void updateConstant(String enname, Integer days, Integer switchh) {
		Connection con = getConnection();
		PreparedStatement ps=null;
		try {
			if(con == null )return ;
			String psql = "update constants set  ";
			if(days!=null){
				psql += "days = "+days+" where enname = '"+enname+"'";
			}else if(switchh!=null){
				psql += "switch = "+ switchh +" where enname = '"+enname+"'";
			}
			
			ps= con.prepareStatement(psql);
			int executeUpdate = ps.executeUpdate();
			log.info("updateConstant:ennname:"+enname+",days:"+days+",switch:"+switchh+",executeUpdate:"+executeUpdate+",sql:"+psql);
			return ;
		} catch (Exception e) {
			log.error("error",e);
			return ;
		}finally{
			try { if(ps!=null)ps.close();} catch (Exception e2) {}
			try { if(con!=null)con.close();} catch (Exception e2) {}
		}
	}
	
}
