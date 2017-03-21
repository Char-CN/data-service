package org.blazer.scheduler.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DateUtil {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
	public static final SimpleDateFormat yyyy_MM_dd_HH_mm_ss_SSS = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");

	public static Date newDate() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	public static Date newDate(Integer addSeconds) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.SECOND, 0);
		c.add(Calendar.SECOND, addSeconds);
		return c.getTime();
	}

	public static String newDateStr_yyyy_MM_dd_HH_mm_ss_SSS() {
		return yyyy_MM_dd_HH_mm_ss_SSS.format(new Date());
	}

	public static String newDateStr() {
		return sdf.format(newDate());
	}

	public static String newDateStrNextMinute() {
		return sdf.format(newDate(60));
	}

	public static String showDate(Date date) {
		return sdf.format(date);
	}

	public static int getSeconds() {
		return Calendar.getInstance().get(Calendar.SECOND);
	}

	public static final HashMap<Integer, Integer> WEEK2FIELD = new HashMap<Integer, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put(1, Calendar.MONDAY);
			put(2, Calendar.TUESDAY);
			put(3, Calendar.WEDNESDAY);
			put(4, Calendar.THURSDAY);
			put(5, Calendar.FRIDAY);
			put(6, Calendar.SATURDAY);
			// 让 7 和 0 都表示为星期日;
			put(7, Calendar.SUNDAY);
			put(0, Calendar.SUNDAY);
		}
	};

	public static final HashMap<Integer, Integer> FIELD2WEEK = new HashMap<Integer, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put(Calendar.MONDAY, 1);
			put(Calendar.TUESDAY, 2);
			put(Calendar.WEDNESDAY, 3);
			put(Calendar.THURSDAY, 4);
			put(Calendar.FRIDAY, 5);
			put(Calendar.SATURDAY, 6);
			// 让 7 和 0 都表示为星期日;
			put(Calendar.SUNDAY, 7);
			put(Calendar.SUNDAY, 0);
		}
	};

}
