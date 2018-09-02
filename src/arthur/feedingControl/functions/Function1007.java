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
 *	addevent 
 */

public class Function1007 extends BaseFunction{
	@Override
	public Result execute() {
		Result result = new Result();
//		console.log(event_no+":"+boar+":"+eventDate+":"+remark+":"+executor+":::"+id);
		String id = getStrParameter("id");
		String event_no = getStrParameter("event_no");
		String boar = getStrParameter("boar");
		String eventDate = getStrParameter("eventDate");
		String resultS = getStrParameter("result");
		String executor = getStrParameter("executor");
		
		HashMap param = new HashMap();
		param.put("cell_id", id);
		param.put("event_no", event_no);
		param.put("boar", boar);
		param.put("date", eventDate);
		param.put("executor", executor);
		param.put("result", resultS);
		
		EventService es = new EventServiceImp();
		es.addEvent(param);
		
		result.setError_info("成功");
		result.setError_no("0");
		return result;
	}
}
