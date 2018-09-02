package arthur.feedingControl.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arthur.feedingControl.entity.Result;
import arthur.feedingControl.service.ApartmentService;
import arthur.feedingControl.service.ApartmentServiceImp;
import arthur.feedingControl.service.CellsService;
import arthur.feedingControl.service.CellsServiceImp;
import arthur.feedingControl.service.ConstantsService;
import arthur.feedingControl.service.ConstantsServiceImp;
import arthur.feedingControl.service.EventService;
import arthur.feedingControl.service.EventServiceImp;
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;
import arthur.feedingControl.service.ScheduleService;
import arthur.feedingControl.service.ScheduleServiceImp;

/**
 * 
 * @author arthu
 *	get schedule day
 */

public class Function1008 extends BaseFunction{
	@Override
	public Result execute() {
		Result result = new Result();
//		console.log(event_no+":"+boar+":"+eventDate+":"+remark+":"+executor+":::"+id);
		String id = getStrParameter("id");
		int scheduleId = Integer.parseInt(id);
		ScheduleService  ss = new ScheduleServiceImp();
		List<HashMap> scheduleDay = ss.getScheduleDay(scheduleId);
		
		result.setError_info("成功");
		result.setError_no("0");
		result.setData(scheduleDay);
		return result;
	}
}
