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
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;

/**
 * 
 * @author arthur
 *	常量
 */

public class Function1005 extends BaseFunction{
	@Override
	public Result execute() {
		ConstantsService cs = new ConstantsServiceImp();
		List<HashMap> constants = cs.getConstants();
		
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		result.setData(constants);
		return result;
	}
}
