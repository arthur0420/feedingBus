package arthur.feedingControl.main;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import arthur.feedingControl.entity.Result;
import arthur.feedingControl.functions.BaseFunction;

public class RequestProcessor {
	private static Logger logger = Logger.getLogger(BaseFunction.class);
	public static void process(HttpServletRequest request, HttpServletResponse response) {
		Map<String, String[]> pm = request.getParameterMap();
		if(!pm.containsKey("funcNo")){
			Result result = new Result();
			result.setError_no("-1");
			result.setError_info("funcNo 不能为空");
			String string = result.toJson();
			try {
				response.setHeader("Content-type", "text/html;charset=UTF-8");  
				response.setHeader("Access-Control-Allow-Origin","*");
				ServletOutputStream os = response.getOutputStream();
				BufferedOutputStream bos = new BufferedOutputStream(os);
				byte[] bytes = string.getBytes(Charset.forName("UTF-8"));
				bos.write(bytes);
				bos.flush();
				bos.close();
			} catch (Exception e) {
				// TO 500
				logger.error("系统错误");
			}
			return ;
		}
		String funcNo = pm.get("funcNo")[0];
		String className = "arthur.feedingControl.functions.Function"+funcNo;
		try {
			BaseFunction bf = (BaseFunction)BaseFunction.class.forName(className).newInstance();
			bf.setFuncNo(funcNo);
			bf.setRequest(request);
			bf.setResponse(response);
			bf.invoke();
		} catch (Exception e) {
			logger.error("process error",e);
		}
	}
}
