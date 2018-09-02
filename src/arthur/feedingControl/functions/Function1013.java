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
 *	modify schedule 
 */

public class Function1013 extends BaseFunction{
	static Logger log = Logger.getLogger(BaseService.class);
	@Override
	public Result execute() {
		String scheduleId = getStrParameter("scheduleId");
		String offset_absolute = getStrParameter("offset_absolute");
		String offset_relative = getStrParameter("offset_relative");
		String speed = getStrParameter("speed");
		
		HashMap<String, Integer> param = new HashMap<String,Integer>();
		
		if(offset_absolute !=null){
			param.put("offset_absolute", Integer.parseInt(offset_absolute));
		}
		if(offset_relative !=null){
			param.put("offset_relative", Integer.parseInt(offset_relative));
		}
		if(speed !=null){
			param.put("speed", Integer.parseInt(speed));
		}
		
		ScheduleService ss = new ScheduleServiceImp();
		ss.modifyShcedule(Integer.parseInt(scheduleId), param);
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		return result;
	}
}
