package org.blazer.scheduler.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DateUtil {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
	public static final SimpleDateFormat yyyy_MM_dd_HH_mm_ss_SSS = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");

	public static void main(String[] args) {
		Calendar c = Calendar.getInstance();
		System.out.println(Calendar.SUNDAY);
		System.out.println(Calendar.MONDAY);
		System.out.println(Calendar.TUESDAY);
		System.out.println(Calendar.WEDNESDAY);
		System.out.println(Calendar.THURSDAY);
		System.out.println(Calendar.FRIDAY);
		System.out.println(Calendar.SATURDAY);
		System.out.println(c.get(c.DAY_OF_WEEK));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		int realWeek = 3;
		for (int i = 1; i < 100; i ++) {
			System.out.println(realWeekEqualsWeek(realWeek, c.get(Calendar.DAY_OF_WEEK)));
			System.out.println(sdf.format(c.getTime()) + " : 星期 " + realWeek);
			realWeek ++;
			if (realWeek==8) {
				realWeek=1;
			}
			c.add(c.DAY_OF_YEAR, 1);
		}
		
	}

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

	public static int getRealWeek(int week) {
		if (week == Calendar.MONDAY)
			return 1;
		if (week == Calendar.TUESDAY)
			return 2;
		if (week == Calendar.WEDNESDAY)
			return 3;
		if (week == Calendar.THURSDAY)
			return 4;
		if (week == Calendar.FRIDAY)
			return 5;
		if (week == Calendar.SATURDAY)
			return 6;
		// default : 让 7 和 0 都表示为星期日;
		return 7;
	}

	public static boolean realWeekEqualsWeek(int realWeek, int week) {
		// default : 让 7 和 0 都表示为星期日;
		if (realWeek == 0) {
			realWeek = 7;
		}
		week -= 1;
		if (week == 0) {
			week = 7;
		}
		return realWeek == week;
	}

//	public static final HashMap<Integer, Integer> FIELD2WEEK = new HashMap<Integer, Integer>() {
//		private static final long serialVersionUID = 1L;
//		{
//			put(Calendar.MONDAY, 1);
//			put(Calendar.TUESDAY, 2);
//			put(Calendar.WEDNESDAY, 3);
//			put(Calendar.THURSDAY, 4);
//			put(Calendar.FRIDAY, 5);
//			put(Calendar.SATURDAY, 6);
//			// 让 7 和 0 都表示为星期日;
//			put(Calendar.SUNDAY, 7);
//		}
//	};

}
