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
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;

/**
 * 
 * @author arthu
 * 栏位数据修改
 */

public class Function1004 extends BaseFunction{
	@Override
	public Result execute() {
		CellsService cs = new CellsServiceImp();
		
		int skip_time = getIntParameter("skip_time", 0);
		int offset = getIntParameter("offset", 0);
		int switchh = getIntParameter("switch", 0);
		int id = getIntParameter("id", 0);
		
		cs.modifyCell(id, skip_time, offset, switchh);
		
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		return result;
	}
}
