package arthur.feedingControl.utils;

public class StringHelper {
	public static boolean isEmpty(String value){
		if(value == null || "".equals(value) )return true;
		return false;
	}
	public static String[] split(String value,String regex){
		String[] split = value.split(regex);
		return split;
	}
}
