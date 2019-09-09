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
 * 单元列表信息
 */

public class Function1002 extends BaseFunction{
	@Override
	public Result execute() {
		String dataType  = getStrParameter("dataType"); // 4dictionary , detail.
//		String dataType  =  "detail";
		ApartmentService as = new ApartmentServiceImp();
		List<HashMap> apartments = as.getApartments();
		if(dataType!=null && dataType.equals("detail")){ 
			for(int i = 0 ; i< apartments.size(); i++){
				HashMap oneApartment = apartments.get(i);
				String apartmentId = oneApartment.get("id")+"";
				CellsService cs = new  CellsServiceImp();
				HashMap css = cs.getCellsStatistic(apartmentId);
				oneApartment.putAll(css);
			}
		}
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		result.setData(apartments);
		return result;
	}
	public static void main(String[] args) {
		Function1002 f = new Function1002();
		Result execute = f.execute();
		System.out.println(execute.toJson());
	}
}
