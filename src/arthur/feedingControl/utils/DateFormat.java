
package arthur.feedingControl.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormat {
	private static String layout = "yyyy-MM-dd HH:mm";
	private static String shortLayout = "yyyy-MM-dd";
	private static SimpleDateFormat sdf = new SimpleDateFormat(layout);
	private static SimpleDateFormat shortSdf = new SimpleDateFormat(shortLayout);
	public static Date formatDate(String str) throws ParseException{
		Date parse  = null;
		if(str.length() == 10){
			parse = shortSdf.parse(str);
		}else{
			parse = sdf.parse(str);
		}
		
		return parse;
	}
	/**
	 * 计算 point 到今天的天数差。
	 * @return
	 * @throws ParseException 
	 */
	public static int minus(String point) throws ParseException{
		Date formatDate = formatDate(point);
		long time = formatDate.getTime();
		Calendar cl = Calendar.getInstance();
		cl.set(Calendar.HOUR_OF_DAY, 0);
		cl.set(Calendar.MINUTE, 0);
		cl.set(Calendar.SECOND, 0);
		long timeInMillis = cl.getTimeInMillis();
		int days = (int) ((timeInMillis - time) / (1000*3600*24))+1;
		return days;
	}
	public static String toString(Date date ){
		
		String dateString = sdf.format(date);
		return dateString;
	}
	
	public static void main(String[] args) {
		String nowdate = DateFormat.toString(new Date());
		System.out.println(nowdate);
	}
	
}
