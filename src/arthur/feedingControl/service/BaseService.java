package arthur.feedingControl.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;



import com.mchange.v2.c3p0.ComboPooledDataSource;

public class BaseService {
	static Logger log = Logger.getLogger(BaseService.class);
	private static Connection conn;
    private static ComboPooledDataSource ds = new ComboPooledDataSource();
    protected static Connection getConnection() {
        try {
            conn = ds.getConnection();
        } catch (SQLException e) {
        	conn = null;
        	log.error("get connection error",e);
        }
        return conn;
    }
    protected List<HashMap> getList(ResultSet rs) throws Exception{
    	ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		List<HashMap> list = new ArrayList<HashMap>();
		while(rs.next()){
			HashMap row = new HashMap();
			for (int i = 1; i <= columnCount; i++) {
				String key= metaData.getColumnLabel(i);
				int columnType = metaData.getColumnType(i);
				switch(columnType){
					case Types.VARCHAR :
						row.put(key,rs.getString(key));
						break;
					case Types.INTEGER :
						row.put(key,rs.getInt(key));
						break;
				}
			}
			list.add(row);
		}
		return list;
	}
}
