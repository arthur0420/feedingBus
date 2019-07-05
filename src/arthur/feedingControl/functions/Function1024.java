package arthur.feedingControl.functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import arthur.feedingControl.entity.Result;
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;
import arthur.feedingControl.service.ScheduleService;
import arthur.feedingControl.service.ScheduleServiceImp;
import arthur.feedingControl.service.UserService;
import arthur.feedingControl.service.UserServiceImp;
import arthur.feedingControl.utils.ConvertHelper;

/**
 * 
 * @author arthur
 *	get record
 */

public class Function1024 extends BaseFunction{
	static Logger log = Logger.getLogger(Function1024.class);
	@Override
	public Result execute() {
		String cellid =getStrParameter("cellid");
		
		LogService ls = new LogServiceImp();
		List<HashMap> record = ls.getRecord(cellid, 1,10);
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		result.setData(record);
		return result;
	}
}
