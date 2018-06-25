package arthur.feedingControl.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;


public class ApartmentServiceImp extends BaseService implements
		ApartmentService {
	static Logger log = Logger.getLogger(ApartmentServiceImp.class);
	@Override
	public List<HashMap> getApartments() {
		
		Connection con = getConnection();
		PreparedStatement ps=null;
		ResultSet r = null;
		try {
			if(con == null )return null;
			String psql = " select * from apartment ";
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
