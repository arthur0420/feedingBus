package arthur.feedingControl.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface LogService {
	public List<HashMap> getLogs(int pageIndex,int pageSize,String dateStart,String dateEnd,String level);
}
