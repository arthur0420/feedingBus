package arthur.feedingControl.test;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import arthur.feedingControl.functions.BaseFunction;

public class TestProcessor {
	private static Logger logger = Logger.getLogger(TestProcessor.class);
	public static void process(Map<String, String> pm) {
		String funcNo = pm.get("funcNo");
		String className = "arthur.feedingControl.functions.Function"+funcNo;
		try {
			BaseFunction bf = (BaseFunction)BaseFunction.class.forName(className).newInstance();
			bf.setFuncNo(funcNo);
			bf.testInvoke();
		} catch (Exception e) {
			logger.error("process error",e);
		}
	}
}
