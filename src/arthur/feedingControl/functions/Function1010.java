package arthur.feedingControl.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

/**
 * 
 * @author arthur
 *	常量 修改
 */

public class Function1010 extends BaseFunction{
	static Logger log = Logger.getLogger(BaseService.class);
	@Override
	public Result execute() {
		String enname = getStrParameter("enname");
		String days = getStrParameter("days");
		String switchh = getStrParameter("switchh");
		
		Integer d = null;
		Integer s = null;
		try {
			if(days !=null){
				d = Integer.parseInt(days);
			}
			if(switchh !=null){
				s = Integer.parseInt(switchh);
			}
		} catch (Exception e) {
			log.error("参数格式化错误",e);
			Result result = new Result();
			result.setError_info("参数格式化错误");
			result.setError_no("-1");
			return result;
		}
		
		ConstantsService cs = new ConstantsServiceImp();
		cs.updateConstant(enname,d,s);
		
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		return result;
	}
}
