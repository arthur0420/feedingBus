package arthur.feedingControl.functions;

import java.util.Map;

import org.apache.log4j.Logger;

import arthur.feedingControl.entity.Result;
import arthur.feedingControl.service.UserService;
import arthur.feedingControl.service.UserServiceImp;
import arthur.feedingControl.utils.ConvertHelper;

/**
 * 
 * @author arthur
 *	add user
 */

public class Function1022 extends BaseFunction{
	static Logger log = Logger.getLogger(Function1022.class);
	@Override
	public Result execute() {
		
		String username = getStrParameter("username");
		String psw = getStrParameter("psw");
		String oldpsw = getStrParameter("oldpsw");
		Result result = new Result();
		
		UserService us = new UserServiceImp();
		
		Map<String, String> queryUser =us.queryUser(username);
		String md5OldPassword = queryUser.get("password");
		boolean verify = ConvertHelper.verify(username, oldpsw, md5OldPassword);
		if(!verify){
			result.setError_info("旧密码错误，修改密码失败");
			result.setError_no("-1");
			return result;
		}
		
		
		us.modifyPassword(username , psw);
		result.setError_info("成功");
		result.setError_no("0");
		return result;
	}
}