package arthur.feedingControl.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arthur.feedingControl.entity.Result;
import arthur.feedingControl.service.CellsService;
import arthur.feedingControl.service.CellsServiceImp;
import arthur.feedingControl.service.EventService;
import arthur.feedingControl.service.EventServiceImp;
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;

/**
 * 
 * @author arthu
 *添加动物
 */

public class Function1016 extends BaseFunction{
	@Override
	public Result execute() {
		Result result = new Result();
		String apId = getStrParameter("apId");
		String no = getStrParameter("no");
		String eventDate = getStrParameter("eventDate");
		
		CellsService cs = new CellsServiceImp();
		List<HashMap> cells = cs.getCells(apId, no);
		if(cells.size() == 0){
			result.setError_info("不存在此栏位，请检查后重试");
			result.setError_no("-1");
			return result;
		}
		HashMap hashMap = cells.get(0);
		int have_animal = (int)hashMap.get("have_animal");
		if(have_animal == 1){
			result.setError_info("此栏位已有动物，请检查后重试");
			result.setError_no("-1");
			return result;
		}
		int id = (int) hashMap.get("id");
		cs.initCell(id);
		
		EventService es = new EventServiceImp();
		HashMap param = new HashMap();
		param.put("cell_id", id);
		param.put("event_no", "2");
		param.put("date", eventDate);
		
		es.addEvent(param);
		
		
		result.setError_info("成功");
		result.setError_no("0");
		return result;
	}
}
