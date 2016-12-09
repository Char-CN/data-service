package org.blazer.scheduler.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExceutorTest2 extends TimerTask {

	private String jobName = "";

	public ScheduledExceutorTest2(String jobName) {
		super();
		this.jobName = jobName;
	}

	@Override
	public void run() {
		System.out.println("Date = " + new Date() + ", execute " + jobName);
	}

	/**
	 * 计算从当前时间currentDate开始，满足条件dayOfWeek, hourOfDay, minuteOfHour,
	 * secondOfMinite的最近时间
	 * 
	 * @return
	 */
	public Calendar getEarliestDate(Calendar currentDate, int dayOfWeek, int hourOfDay, int minuteOfHour, int secondOfMinite) {
		// 计算当前时间的WEEK_OF_YEAR,DAY_OF_WEEK, HOUR_OF_DAY, MINUTE,SECOND等各个字段值
		int currentWeekOfYear = currentDate.get(Calendar.WEEK_OF_YEAR);
		int currentDayOfWeek = currentDate.get(Calendar.DAY_OF_WEEK);
		int currentHour = currentDate.get(Calendar.HOUR_OF_DAY);
		int currentMinute = currentDate.get(Calendar.MINUTE);
		int currentSecond = currentDate.get(Calendar.SECOND);

		// 如果输入条件中的dayOfWeek小于当前日期的dayOfWeek,则WEEK_OF_YEAR需要推迟一周
		boolean weekLater = false;
		if (dayOfWeek < currentDayOfWeek) {
			weekLater = true;
		} else if (dayOfWeek == currentDayOfWeek) {
			// 当输入条件与当前日期的dayOfWeek相等时，如果输入条件中的
			// hourOfDay小于当前日期的
			// currentHour，则WEEK_OF_YEAR需要推迟一周
			if (hourOfDay < currentHour) {
				weekLater = true;
			} else if (hourOfDay == currentHour) {
				// 当输入条件与当前日期的dayOfWeek, hourOfDay相等时，
				// 如果输入条件中的minuteOfHour小于当前日期的
				// currentMinute，则WEEK_OF_YEAR需要推迟一周
				if (minuteOfHour < currentMinute) {
					weekLater = true;
				} else if (minuteOfHour == currentSecond) {
					// 当输入条件与当前日期的dayOfWeek, hourOfDay，
					// minuteOfHour相等时，如果输入条件中的
					// secondOfMinite小于当前日期的currentSecond，
					// 则WEEK_OF_YEAR需要推迟一周
					if (secondOfMinite < currentSecond) {
						weekLater = true;
					}
				}
			}
		}
		if (weekLater) {
			// 设置当前日期中的WEEK_OF_YEAR为当前周推迟一周
			currentDate.set(Calendar.WEEK_OF_YEAR, currentWeekOfYear + 1);
		}
		// 设置当前日期中的DAY_OF_WEEK,HOUR_OF_DAY,MINUTE,SECOND为输入条件中的值。
		currentDate.set(Calendar.DAY_OF_WEEK, dayOfWeek);
		currentDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
		currentDate.set(Calendar.MINUTE, minuteOfHour);
		currentDate.set(Calendar.SECOND, secondOfMinite);
		return currentDate;

	}

	// public static void main(String[] args) throws Exception {
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	// ScheduledExceutorTest2 test = new ScheduledExceutorTest2("job1");
	// // 获取当前时间
	// Calendar currentDate = Calendar.getInstance();
	// long currentDateLong = currentDate.getTime().getTime();
	// System.out.println("Current Date = " +
	// sdf.format(currentDate.getTime()));
	// // 计算满足条件的最近一次执行时间
	// Calendar earliestDate = test.getEarliestDate(currentDate, 3, 16, 38, 10);
	// long earliestDateLong = earliestDate.getTime().getTime();
	// System.out.println("Earliest Date = " +
	// sdf.format(earliestDate.getTime()));
	// // 计算从当前时间到最近一次执行时间的时间间隔
	// long delay = earliestDateLong - currentDateLong;
	// // 计算执行周期为一星期
	// long period = 7 * 24 * 60 * 60 * 1000;
	// ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
	// // 从现在开始delay毫秒之后，每隔一星期执行一次job1
	// service.scheduleAtFixedRate(test, delay, period, TimeUnit.MILLISECONDS);
	//
	// }

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		Date date = c.getTime();
		date.setSeconds(0);

//		date = getNextMonthStep(date, 12);
//		System.out.println(sdf.format(date));
		
//		date = getNextDayStep(date, 4);
//		System.out.println(sdf.format(date));
		
		date = getNextHourStep(date, 15);
		System.out.println(sdf.format(date));

		System.out.println(18 % 15);
		
//		Calendar c = Calendar.getInstance();
//		
//		Date date = c.getTime();
//		date.setSeconds(0);
//
//		date = getNextWeek(date, 3);
//		System.out.println(sdf.format(date));
//		
//		date = getNextMonth(date, 5);
//		System.out.println(sdf.format(date));
//		
//		date = getNextDay(date, 10);
//		System.out.println(sdf.format(date));
//
//		date = getNextHour(date, 19);
//		System.out.println(sdf.format(date));
//
//		date = getNextMinute(date, 22);
//		System.out.println(sdf.format(date));
		
//		long l1 = c.getTime().getTime();
//
//		// c.set(Calendar.YEAR, 2017);
//		//
//		// c.set(Calendar.MONTH, 0);
//		// c.set(Calendar.DAY_OF_WEEK_IN_MONTH, 7);
//		// c.set(Calendar.HOUR_OF_DAY, 1);
//		// c.set(Calendar.DAY_OF_MONTH, 1);
//		// c.set(Calendar.MINUTE, 1);
//		System.out.println(sdf.format(c.getTime()));
//		c.set(Calendar.DAY_OF_WEEK, 0);
//		System.out.println(sdf.format(c.getTime()));
//
//		long l2 = c.getTime().getTime();
//
//		System.out.println(l1);
//		System.out.println(l2);
//		if (l2 <= l1) {
//			System.out.println("++++1");
//			c.add(Calendar.DAY_OF_YEAR, 7);
//		}
//
//		System.out.println("=============================");
//		System.out.println(sdf.format(c.getTime()));

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
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int mod = 24 % step;
		System.out.println("mod : " + mod);
		if (mod == 0) {
			return c.getTime();
		}
		c.add(Calendar.HOUR_OF_DAY, step - mod);
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

}
