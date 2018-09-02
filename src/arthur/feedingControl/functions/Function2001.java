package arthur.feedingControl.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import arthur.feedingControl.device.DeviceControl;
import arthur.feedingControl.entity.Result;
import arthur.feedingControl.service.LogService;
import arthur.feedingControl.service.LogServiceImp;

/**
 * 
 * @author arthu
 * 测速
 */

public class Function2001 extends BaseFunction{
	static Logger log = Logger.getLogger(Function2001.class);
	@Override
	public Result execute() {
		log.info("2001");
		DeviceControl dc = new DeviceControl();
		log.info("dc init");
		try {
			dc.openMachine(1, 60);
		} catch (Exception e) {
			log.error(e);
			Result result = new Result();
			result.setError_info("失败");
			result.setError_no("-1");
			return result;
		}
		Result result = new Result();
		result.setError_info("成功");
		result.setError_no("0");
		return result;
	}
}
