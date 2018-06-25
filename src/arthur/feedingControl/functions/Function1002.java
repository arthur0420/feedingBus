package arthur.feedingControl.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arthur.feedingControl.entity.Result;
import arthur.feedingControl.service.ApartmentService;
import arthur.feedingControl.service.ApartmentServiceImp;
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;

/**
 * 
 * @author arthu
 * 单元列表信息
 */

public class Function1002 extends BaseFunction{
	@Override
	public Result execute() {
		ApartmentService as = new ApartmentServiceImp();
		List<HashMap> apartments = as.getApartments();
		
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		result.setData(apartments);
		return result;
	}
}
