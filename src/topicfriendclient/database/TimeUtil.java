package topicfriendclient.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

	private static String sTimeFormat = "yyyy-MM-dd HH:mm:ss";
	
	public static long getCurrentTimestamp() {
		return System.currentTimeMillis();
	}
	
	public static String getCurrentTimeString() {
		return convertTimestampToString(getCurrentTimestamp());
	}
	
	public static String convertTimestampToString(long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat(sTimeFormat, Locale.getDefault());
		String date = sdf.format(new Date(timeStamp/**1000L*/));
		System.out.println(date);
		return date;
	}
	
	public static long convertStringToTimestamp(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat(sTimeFormat, Locale.getDefault());
		try {
			Date d = sdf.parse(date);
			return d.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}
}
