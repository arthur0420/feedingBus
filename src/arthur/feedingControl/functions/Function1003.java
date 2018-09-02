package arthur.feedingControl.functions;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import arthur.feedingControl.entity.Result;
import arthur.feedingControl.service.CellsService;
import arthur.feedingControl.service.CellsServiceImp;

/**
 * 
 * @author arthu
 * 动物列表信息
 */

public class Function1003 extends BaseFunction{
	static Logger log = Logger.getLogger(Function1003.class);
	private CellsService  cs = null;
	@Override
	public Result execute() {
		
		String apartmentId = getStrParameter("apartmentId");
		cs= new CellsServiceImp();
		List<HashMap> cells = cs.getCells(apartmentId, null);
		
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		result.setData(cells);
		return result;
	}
}
