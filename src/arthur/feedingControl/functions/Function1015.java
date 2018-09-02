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

public class Function1015 extends BaseFunction{
	static Logger log = Logger.getLogger(Function1015.class);
	@Override
	public Result execute() {
		String scheduleId = getStrParameter("scheduleId");
		String data = getStrParameter("data");
		@SuppressWarnings("deprecation")
		JSONArray dataJson = JSONArray.fromString(data);
		
		ScheduleService ss = new ScheduleServiceImp();
		
		ss.modifyShceduleHour(Integer.parseInt(scheduleId), dataJson);
		
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		return result;
	}
}
