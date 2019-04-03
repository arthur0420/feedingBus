package arthur.feedingControl.utils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;


import arthur.feedingControl.service.ConstantsService;
import arthur.feedingControl.service.ConstantsServiceImp;

public class Config {
	private static Logger logger = Logger.getLogger(Config.class);
	private static HashMap<String,HashMap> constants = new HashMap<String,HashMap>();
	private static Properties bcConfig = null;
	public static HashMap getConstant(String enname){
		//TODO 初始化，从数据库中取 constants表的数据。
		
		if(constants.containsKey(enname)){
			HashMap hashMap = constants.get(enname);
			return hashMap;
		}else{
			logger.error("constants do not contains the key:"+enname);
			return null;
		}
	};
	
	public  static void initConfig(){
		
		ConstantsService cs = new ConstantsServiceImp();
		List<HashMap> list = cs.getConstants();
		HashMap<String,HashMap> temp = new HashMap<String,HashMap>();
		for (int i = 0; i < list.size(); i++) {
			HashMap  one = list.get(i);
			String enname= one.get("enname")+"";
			temp.put(enname, one);
		}
		constants = temp;
		try {
			InputStream resourceAsStream = Config.class.getClassLoader().getResourceAsStream("config.properties");
			bcConfig = new Properties();
			bcConfig.load(resourceAsStream);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static String getBcConfig(String key) {
		String property = bcConfig.getProperty(key)+"";
		return property;
	}
}