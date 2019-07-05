package arthur.feedingControl.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.javafx.collections.MappingChange.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 
 * @author arthu
 * 结果类。
 */
public class Result {
	String error_no=""; //0成功 -1失败
	String error_info="";// 错误信息
	private JSONObject js = new JSONObject();
	public void setError_no(String error_no) {
		this.error_no = error_no;
	}
	public void setError_info(String error_info) {
		this.error_info = error_info;
	}
	public void setData(List<HashMap> data) {
		setData("data", data);
	}
	public void setData(HashMap data) {
		setData("data", data);
	}
	public void setData(String key,List data) {
		JSONArray jsond = new JSONArray();
		jsond.addAll(data);
		js.put(key,jsond);
	}
	public void setData(String key,HashMap data) {
		JSONArray jsond = new JSONArray();
		jsond.add(data);
		js.put(key,jsond);
	}
	public void setString(String key,String value) {
		js.put(key,value);
	}
	public String toJson(){
		js.put("error_no", error_no);
		js.put("error_info", error_info);
		return js.toString();
	}
	/*public static void main(String[] args) {
		Result result = new Result();
		
		result.setError_no("0");
		result.setError_info("处理成功");
		ArrayList<HashMap<String, String>> md = new ArrayList<HashMap<String, String>>();
		HashMap<String,String> hashMap = new HashMap<String,String>();
		hashMap.put("age", "11");
		hashMap.put("name", "arthur");
		md.add(hashMap);
		hashMap = new HashMap<String,String>();
		hashMap.put("age", "12");
		hashMap.put("name", "lily");
		md.add(hashMap);
		result.setData(md);
		String json = result.toJson();
		System.out.println(json);
	}*/
}
