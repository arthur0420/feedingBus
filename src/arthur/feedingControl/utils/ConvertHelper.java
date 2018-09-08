package arthur.feedingControl.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class ConvertHelper {
		public static String   md5(String user,String password){
			
			String encodeStr=DigestUtils.md5Hex(user + password);
			
			return encodeStr;
		}
		
		public static boolean verify(String text, String key, String md5){
	        //根据传入的密钥进行验证
	        String md5Text = md5(text, key);
	        if(md5Text.equalsIgnoreCase(md5))
	        {
	            return true;
	        }
	            return false;
	    }
		public static void main(String[] args) {
			String md5 = md5("admin", "admin");
			System.out.println(md5);
		}
}
