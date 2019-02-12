package arthur.feedingControl.functions;

import org.apache.log4j.Logger;

import arthur.feedingControl.device.DeviceControl;
import arthur.feedingControl.entity.Result;
import arthur.feedingControl.utils.Config;

/**
 * 
 * @author arthu
 * 测速
 */

public class Function2001 extends BaseFunction{
	static Logger log = Logger.getLogger(Function2001.class);
	@Override
	public Result execute() {
		Result result = new Result();
		try {
			DeviceControl dc = DeviceControl.getInstance();
			if(dc == null) {
				result.setError_info("有任务正在执行。");
				result.setError_no("-1");
				return result;
			}
			String defaultportid = Config.getBcConfig("defaultportid");
			dc.sendSingleTask(defaultportid, 60);
			DeviceControl.close();
//			dc.openMachine(1, 60);
		} catch (Exception e) {
			log.error(e);
			result.setError_info("失败");
			result.setError_no("-1");
			return result;
		}
		result.setError_info("成功");
		result.setError_no("0");
		return result;
	}
}
