package arthur.feedingControl.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

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
import arthur.feedingControl.service.ScheduleService;
import arthur.feedingControl.service.ScheduleServiceImp;
import arthur.feedingControl.service.UserService;
import arthur.feedingControl.service.UserServiceImp;
import arthur.feedingControl.utils.ConvertHelper;

/**
 * 
 * @author arthur
 *	login
 */

public class Function1017 extends BaseFunction{
	static Logger log = Logger.getLogger(Function1017.class);
	@Override
	public Result execute() {
		Result result = new Result();
		String username= getStrParameter("username");
		String password = getStrParameter("password");
		
		UserService us = new UserServiceImp();
		HashMap<String, String> user = (HashMap<String, String>)us.queryUser(username);
		if(user == null){
			result.setError_info("该用户名不存在");
			result.setError_no("-1");
			return result;
		}
		String md5Psw = user.get("password");
		boolean verify = ConvertHelper.verify(username, password, md5Psw);
		if(!verify){
			result.setError_info("密码错误");
			result.setError_no("-1");
			return result;
		}
		user.remove("password");
		result.setData(user);
		result.setError_info("成功");
		result.setError_no("0");
		return result;
	}
}
