package arthur.feedingControl.functions;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import arthur.feedingControl.entity.Result;
import arthur.feedingControl.utils.StringHelper;

public abstract class BaseFunction {
	private static Logger logger = Logger.getLogger(BaseFunction.class);
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected String funcNo = "";
	long costTime = 0l;
	
	public void setRequest(HttpServletRequest request){
		this.request = request;
	}
	public void setResponse(HttpServletResponse response){
		this.response = response;
	}
	public void setFuncNo(String funcNo){
		this.funcNo = funcNo;
	}
	public void before(){}
	public void after(){}
	public void invoke() {
		// System.out.println("this is new invoke==============");
		try {
			long t1 = System.currentTimeMillis();
			before();
			Result r = execute();
			packet(r);
			long t2 = System.currentTimeMillis();
			costTime = t2 - t1;
			logger.info("调用功能号" +funcNo + "花费时间:"
					+ (t2 - t1));
		} catch (Exception e) {
			logger.error("invoke", e);
		}
		finally {
			after();
		}
	}
	public String  testInvoke() {
		// System.out.println("this is new invoke==============");
		try {
			long t1 = System.currentTimeMillis();
			before();
			Result r = execute();
			String string = r.toJson();
			
//			packet(r);
			long t2 = System.currentTimeMillis();
			costTime = t2 - t1;
			logger.info("调用功能号" +funcNo + "花费时间:"
					+ (t2 - t1));
			return string;
		} catch (Exception e) {
			logger.error("invoke", e);
		}
		finally {
			after();
		}
		return "testInvoke null";
	}
	public abstract Result execute();
	void packet(Result result) throws IOException{
		String string = result.toJson();
		response.setHeader("Content-type", "text/html;charset=UTF-8");  
		response.setHeader("Access-Control-Allow-Origin","*");
		ServletOutputStream os = response.getOutputStream();
		BufferedOutputStream bos = new BufferedOutputStream(os);
		byte[] bytes = string.getBytes(Charset.forName("UTF-8"));
		bos.write(bytes);
		bos.flush();
		bos.close();
	} 
	public String getStrParameter(String fieldName) {
		String value = this.request.getParameter(fieldName);
//		byte[] bytes;  不需要 中文转码
//		try {
//			if(value == null) return "";
//			bytes = value.getBytes("ISO-8859-1");
//			value = new String(bytes,"UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		return ((StringHelper.isEmpty(value)) ? "" : value);
	}
	public String getStrParameter(String fieldName, String defaultValue) {
		String value = this.request.getParameter(fieldName);
//		byte[] bytes;
//		try {
//			if(value == null) return "";
//			bytes = value.getBytes("ISO-8859-1");
//			value = new String(bytes,"UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		return ((StringHelper.isEmpty(value)) ? defaultValue : value);
	}
	public String[] getStrArrayParameter(String fieldName) {
		String value = getStrParameter(fieldName);
		if(value == null)value ="";
		return StringHelper.split(value, ",");
	}
	public int getIntParameter(String fieldName,int defaultValue){
		int v = defaultValue;
		String p= getStrParameter(fieldName);
		if(p == null || "".equals(p))return v;
		try {
			v = Integer.parseInt(p);
		} catch (Exception e) {
			logger.error("error value,format fail",e);
			return v;
		}
		return v ;
		
		
	}
}
