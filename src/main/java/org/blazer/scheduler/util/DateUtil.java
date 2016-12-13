package org.blazer.scheduler.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DateUtil {

	public static Date newDate() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.SECOND, 0);
		return c.getTime();
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
			put(7, Calendar.SUNDAY);
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
			put(Calendar.SUNDAY, 7);
		}
	};

}
