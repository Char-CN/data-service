package org.blazer.scheduler.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.scheduler.model.TimeModel;
import org.blazer.scheduler.util.DateUtil;

/**
 * cron表达式解释
 * 
 * * * * * *
 * 
 * - - - - -
 * 
 * | | | | |
 * 
 * | | | | +-- day of week (0 - 7) (Sunday=0 or 7)
 * 
 * | | | +---- month (1 - 12)
 * 
 * | | +------ day of month (1 - 31)
 * 
 * | +-------- hour (0 - 23)
 * 
 * +---------- min (0 - 59)
 * 
 */
public class ExpressionUtil {

	private static final String R1 = "[*]";
	private static final String R2 = "[*]/\\d+";
	private static final String R3 = "\\d+";

	private static final String ONE = "([*]|[*]/\\d+|\\d+)";

	private static final String BLANK = "\\s+";

	// Minute Hour Day Month Weekday
	private static final String EXPRESSION = ONE + BLANK + ONE + BLANK + ONE + BLANK + ONE + BLANK + ONE;

	private static final Pattern pattern = Pattern.compile(EXPRESSION);

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = "* 5 * * *";
		System.out.println(check(str));

		String[] arr = toArray(str);
		for (String a : arr) {
			System.out.println(a);
		}

		System.out.println(sdf.format(getNextDate(DateUtil.newDate(), toArray(str))));
	}

	public static String[] toArray(String expression) {
		Matcher m = pattern.matcher(expression);
		String[] arr = new String[m.groupCount()];
		if (m.find()) {
			for (int i = 0; i < arr.length; i++) {
				arr[i] = m.group(i + 1);
			}
		}
		return arr;
	}

	public static Date getNextDate(Date date, String[] array) {
		// 最小粒度每分钟执行1次
		// */2 * * * *
		// */2 * * * *
		String minute = array[0];
		String hour = array[1];
		String day = array[2];
		String month = array[3];
		String weekday = array[4];
		Date _date = new Date(date.getTime());
		if (weekday.matches(R1)) {
			// do nothing
		}
		if (weekday.matches(R2)) {
			String[] strs = weekday.split("/");
			_date = DateUtil.getNextWeekStep(_date, IntegerUtil.getInt0(strs[1]));
		}
		if (weekday.matches(R3)) {
			_date = DateUtil.getNextWeek(_date, IntegerUtil.getInt0(weekday));
		}


		if (month.matches(R1)) {
			// do nothing
		}
		if (month.matches(R2)) {
			String[] strs = month.split("/");
			_date = DateUtil.getNextMonthStep(_date, IntegerUtil.getInt0(strs[1]));
		}
		if (month.matches(R3)) {
			_date = DateUtil.getNextMonth(_date, IntegerUtil.getInt0(month));
		}


		if (day.matches(R1)) {
			// do nothing
		}
		if (day.matches(R2)) {
			String[] strs = day.split("/");
			_date = DateUtil.getNextDayStep(_date, IntegerUtil.getInt0(strs[1]));
		}
		if (day.matches(R3)) {
			_date = DateUtil.getNextDay(_date, IntegerUtil.getInt0(day));
		}


		if (hour.matches(R1)) {
			// do nothing
		}
		if (hour.matches(R2)) {
			String[] strs = hour.split("/");
			_date = DateUtil.getNextHourStep(_date, IntegerUtil.getInt0(strs[1]));
		}
		if (hour.matches(R3)) {
			_date = DateUtil.getNextHour(_date, IntegerUtil.getInt0(hour));
		}


		if (minute.matches(R1)) {
			// 从第一分钟起
			// do nothing
		}
		if (minute.matches(R2)) {
			String[] strs = minute.split("/");
			_date = DateUtil.getNextMinuteStep(_date, IntegerUtil.getInt0(strs[1]));
		}
		if (minute.matches(R3)) {
			_date = DateUtil.getNextMinute(_date, IntegerUtil.getInt0(minute));
		}
		
		
		return _date;
//		return getNextDate(array, tm, 1).get(0);
	}

	public static List<TimeModel> getNextDate(String[] array, TimeModel tm, int nextCount) {

		return null;
	}
	
	public static void getNextDate(List<TimeModel> list , String[] array, TimeModel tm, int nextCount) {
		
	}

	public static boolean check(String expression) {
		return expression.matches(EXPRESSION);
	}

}
