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

/**
 * 
 * @author arthu
 *	event get list
 */

public class Function1006 extends BaseFunction{
	@Override
	public Result execute() {
		Result result = new Result();
		String id = getStrParameter("id");
		if(id== null || id.equals("")){
			result.setError_info("id 不能为空");
			result.setError_no("-1");
			return result;
		}
		EventService es = new EventServiceImp();
		List<HashMap> events = es.getEvent(id);
		
		result.setError_info("成功");
		result.setError_no("0");
		result.setData(events);
		return result;
	}
}
