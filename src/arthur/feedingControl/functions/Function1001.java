package arthur.feedingControl.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arthur.feedingControl.entity.Result;
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;

/**
 * 
 * @author arthu
 * 关注提醒，日志警告
 */

public class Function1001 extends BaseFunction{
	@Override
	public Result execute() {
		int pageIndex = getIntParameter("pageIndex",1);
		int pageSize = getIntParameter("pageSize",10);
		LogService ls = new LogServiceImp();
		List<HashMap> logs =(ArrayList<HashMap>)ls.getLogs(pageIndex, pageSize, null, null, null);
		int logCount = ls.getLogCount();
		
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		result.setData(logs);
		result.setString("total",logCount+"");
		return result;
	}
}
