package arthur.feedingControl.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface LogService {
	public List<HashMap> getLogs(int pageIndex,int pageSize,String dateStart,String dateEnd,String level);
	public int getLogCount();
	public void AddLost(String level ,String logText);
	public void addRecord(List<HashMap> data);
	public List<HashMap> getRecord(String cellid,int pageIndex,int pageSize) ;
	public List<HashMap> getRecord() ;
}
