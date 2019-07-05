package arthur.feedingControl.functions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import arthur.feedingControl.entity.Result;
import arthur.feedingControl.service.ScheduleService;
import arthur.feedingControl.service.ScheduleServiceImp;
import arthur.feedingControl.service.UserService;
import arthur.feedingControl.service.UserServiceImp;
import arthur.feedingControl.utils.ConvertHelper;

/**
 * 
 * @author arthur
 *	add user
 */

public class Function1023 extends BaseFunction{
	static Logger log = Logger.getLogger(Function1023.class);
	@Override
	public Result execute() {
		
		ScheduleService ss = new ScheduleServiceImp();
		
		List<HashMap> schedule = ss.getScheduleList();
		
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		result.setData(schedule);
		return result;
	}
}
