package arthur.feedingControl.utils;

import java.util.HashMap;

public class config {
	
	public static HashMap getConstant(String enname){
		//TODO 初始化，从数据库中取 constants表的数据。
		HashMap r = new HashMap();
		r.put("days", 115);
		return r;
	};
}
