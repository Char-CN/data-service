package org.blazer.scheduler.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.blazer.scheduler.model.TimeModel;

public class DateUtil {

	public static final Integer monthStep = 1;
	public static final Integer dayStep = 1;
	public static final Integer hourStep = 1;
	public static final Integer minuteStep = 1;
	public static final Integer weekdayStep = 7;


	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		Date date = c.getTime();
		date.setSeconds(0);

		// date = getNextMonthStep(date, 12);
		// System.out.println(sdf.format(date));

		// date = getNextDayStep(date, 4);
		// System.out.println(sdf.format(date));

		// date = getNextHourStep(date, 11);
		// System.out.println(sdf.format(date));

		 date = getNextMinuteStep(date, 2);
		 System.out.println(sdf.format(date));

//		date = getNextWeekStep(date, 1);
//		System.out.println(sdf.format(date));

		// Calendar c = Calendar.getInstance();
		//
		// Date date = c.getTime();
		// date.setSeconds(0);
		//
		// date = getNextWeek(date, 3);
		// System.out.println(sdf.format(date));
		//
		// date = getNextMonth(date, 5);
		// System.out.println(sdf.format(date));
		//
		// date = getNextDay(date, 10);
		// System.out.println(sdf.format(date));
		//
		// date = getNextHour(date, 19);
		// System.out.println(sdf.format(date));
		//
		// date = getNextMinute(date, 22);
		// System.out.println(sdf.format(date));

		// long l1 = c.getTime().getTime();
		//
		// // c.set(Calendar.YEAR, 2017);
		// //
		// // c.set(Calendar.MONTH, 0);
		// // c.set(Calendar.DAY_OF_WEEK_IN_MONTH, 7);
		// // c.set(Calendar.HOUR_OF_DAY, 1);
		// // c.set(Calendar.DAY_OF_MONTH, 1);
		// // c.set(Calendar.MINUTE, 1);
		// System.out.println(sdf.format(c.getTime()));
		// c.set(Calendar.DAY_OF_WEEK, 0);
		// System.out.println(sdf.format(c.getTime()));
		//
		// long l2 = c.getTime().getTime();
		//
		// System.out.println(l1);
		// System.out.println(l2);
		// if (l2 <= l1) {
		// System.out.println("++++1");
		// c.add(Calendar.DAY_OF_YEAR, 7);
		// }
		//
		// System.out.println("=============================");
		// System.out.println(sdf.format(c.getTime()));

	}


	public static Date newDate() {
		Calendar c = Calendar.getInstance();
		Date date = c.getTime();
		date.setSeconds(0);
		return date;
	}

	// 下一个指定月份
	public static Date getNextMonth(Date date, int month) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		long l1 = c.getTime().getTime();
		c.set(Calendar.MONTH, month - 1);
		long l2 = c.getTime().getTime();
		if (l2 <= l1) {
			c.add(Calendar.YEAR, 1);
		}
		return c.getTime();
	}

	// 下一个月份 根据步长计算
	public static Date getNextMonthStep(Date date, int step) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH) + 1;
		if (month % step == 0) {
			return c.getTime();
		}
		c.add(Calendar.MONTH, step);
		return c.getTime();
	}

	// 下一个指定日
	public static Date getNextDay(Date date, int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		long l1 = c.getTime().getTime();
		c.set(Calendar.DAY_OF_MONTH, day);
		long l2 = c.getTime().getTime();
		if (l2 <= l1) {
			c.add(Calendar.MONTH, 1);
		}
		return c.getTime();
	}

	// 下一个日 根据步长计算
	public static Date getNextDayStep(Date date, int step) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int mod = day % step;
		if (mod == 0) {
			return c.getTime();
		}
		c.add(Calendar.DAY_OF_YEAR, step - mod);
		return c.getTime();
	}

	// 下一个指定小时
	public static Date getNextHour(Date date, int hour) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		long l1 = c.getTime().getTime();
		c.set(Calendar.HOUR_OF_DAY, hour);
		long l2 = c.getTime().getTime();
		if (l2 <= l1) {
			c.add(Calendar.DAY_OF_YEAR, 1);
		}
		return c.getTime();
	}

	// 下一个小时 根据步长计算
	public static Date getNextHourStep(Date date, int step) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// 当前小时
		int hour = c.get(Calendar.HOUR_OF_DAY);
		c.set(Calendar.HOUR_OF_DAY, step);
		// 步长小于等于当前时间，时间+24小时
		if (step <= hour) {
			c.add(Calendar.HOUR_OF_DAY, 24);
		}
		return c.getTime();
	}

	// 下一个指定分钟
	public static Date getNextMinute(Date date, int minute) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		long l1 = c.getTime().getTime();
		c.set(Calendar.MINUTE, minute);
		long l2 = c.getTime().getTime();
		if (l2 <= l1) {
			c.add(Calendar.HOUR_OF_DAY, 1);
		}
		return c.getTime();
	}

	// 下一个分钟 根据步长计算
	public static Date getNextMinuteStep(Date date, int step) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// 当前分钟
		int minute = c.get(Calendar.MINUTE);
		int mod = minute % step;
		c.add(Calendar.MINUTE, mod == 0 ? step : mod);
		return c.getTime();
	}

	// 下一个指定星期几
	public static Date getNextWeek(Date date, int weekday) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		long l1 = c.getTime().getTime();
		c.set(Calendar.DAY_OF_WEEK, weekday + 1);
		long l2 = c.getTime().getTime();
		if (l2 <= l1) {
			c.add(Calendar.DAY_OF_YEAR, 7);
		}
		return c.getTime();
	}

	// 下一个星期几 根据步长计算
	public static Date getNextWeekStep(Date date, int step) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// 设置第一天是星期一
		c.setFirstDayOfWeek(Calendar.MONDAY);
		// 当前星期几
		int week = FIELD2WEEK.get(c.get(Calendar.DAY_OF_WEEK));
		c.set(Calendar.DAY_OF_WEEK, WEEK2FIELD.get(step));
		// 步长大于当前时间，返回步长时间，否则返回步长时间+7天
		if (step <= week) {
			c.add(Calendar.DAY_OF_YEAR, 7);
		}
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

	public static Integer getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	public static Integer getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	public static Integer getDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	public static Integer getHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}

	public static Integer getMinute() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}

	public static Integer getSeconds() {
		return Calendar.getInstance().get(Calendar.SECOND);
	}

	public static Integer getWeekday() {
		Integer num = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		num = num == 1 ? 7 : num - 1;
		return num;
	}

	public static TimeModel now() {
		return new TimeModel(getYear(), getMonth(), getDay(), getHour(), getMinute(), getSeconds());
	}

	public static String currentTime() {
		return "系统时间:" + getYear() + "年" + getMonth() + "月" + getDay() + "日," + getHour() + "时" + getMinute() + "分" + getSeconds() + "秒" + ",星期" + getWeekday();
	}

}
