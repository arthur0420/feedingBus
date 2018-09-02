package arthur.feedingControl.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;

import arthur.feedingControl.entity.Result;
import arthur.feedingControl.service.ApartmentService;
import arthur.feedingControl.service.ApartmentServiceImp;
import arthur.feedingControl.service.BaseService;
import arthur.feedingControl.service.CellsService;
import arthur.feedingControl.service.CellsServiceImp;
import arthur.feedingControl.service.ConstantsService;
import arthur.feedingControl.service.ConstantsServiceImp;
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;
import arthur.feedingControl.service.ScheduleService;
import arthur.feedingControl.service.ScheduleServiceImp;

/**
 * 
 * @author arthur
 *	get schedule hour
 */

public class Function1014 extends BaseFunction{
	static Logger log = Logger.getLogger(BaseService.class);
	@Override
	public Result execute() {
		String scheduleId = getStrParameter("scheduleId");
		
		
		ScheduleService ss = new ScheduleServiceImp();
		
		List<HashMap> schedule = ss.getScheduleHour(Integer.parseInt(scheduleId));
		
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		result.setData(schedule);
		return result;
	}
}
