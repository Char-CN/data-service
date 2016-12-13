package org.blazer.scheduler.core;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.blazer.dataservice.util.IntegerUtil;
import org.blazer.scheduler.model.TimeModel;
import org.blazer.scheduler.util.DateUtilBak;

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
public class ExpressionUtilBak {

	public static final String R1 = "[*]";
	public static final String R2 = "[*]/\\d+";
	public static final String R3 = "\\d+";

	public static final String ONE = "([*]|[*]/\\d+|\\d+)";

	public static final String BLANK = "\\s+";

	// Minute Hour Day Month Weekday
	public static final String EXPRESSION = ONE + BLANK + ONE + BLANK + ONE + BLANK + ONE + BLANK + ONE;

	public static final Pattern pattern = Pattern.compile(EXPRESSION);

	public static void main(String[] args) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = null;
		Date next = null;

		str = "* * * * *";
		System.out.println(str + "|" + check(str));
		next = getNextDate(DateUtilBak.newDate(), toArray(str));
		System.out.println("next time:" + sdf.format(next));
		next = getNextDate(next, toArray(str));
		System.out.println("next time:" + sdf.format(next));
		next = getNextDate(next, toArray(str));
		System.out.println("next time:" + sdf.format(next));
		System.out.println();

		str = "5 * * * *";
		System.out.println(str + "|" + check(str));
		next = getNextDate(DateUtilBak.newDate(), toArray(str));
		System.out.println("next time:" + sdf.format(next));
		next = getNextDate(next, toArray(str));
		System.out.println("next time:" + sdf.format(next));
		next = getNextDate(next, toArray(str));
		System.out.println("next time:" + sdf.format(next));
		System.out.println();
		
		str = "*/5 * * * *";
		System.out.println(str + "|" + check(str));
		next = getNextDate(DateUtilBak.newDate(), toArray(str));
		System.out.println("next time:" + sdf.format(next));
		next = getNextDate(next, toArray(str));
		System.out.println("next time:" + sdf.format(next));
		next = getNextDate(next, toArray(str));
		System.out.println("next time:" + sdf.format(next));
		System.out.println();

		str = "* 5 * * *";
		System.out.println(str + "|" + check(str));
		next = getNextDate(DateUtilBak.newDate(), toArray(str));
		System.out.println("next time:" + sdf.format(next));
		next = getNextDate(next, toArray(str));
		System.out.println("next time:" + sdf.format(next));
		next = getNextDate(next, toArray(str));
		System.out.println("next time:" + sdf.format(next));
		System.out.println();

		str = "* */5 * * *";
		System.out.println(str + "|" + check(str));
		next = getNextDate(DateUtilBak.newDate(), toArray(str));
		System.out.println("next time:" + sdf.format(next));
		next = getNextDate(next, toArray(str));
		System.out.println("next time:" + sdf.format(next));
		next = getNextDate(next, toArray(str));
		System.out.println("next time:" + sdf.format(next));
		System.out.println();
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
			_date = DateUtilBak.getNextWeekStep(_date, IntegerUtil.getInt0(strs[1]));
		}
		if (weekday.matches(R3)) {
			_date = DateUtilBak.getNextWeek(_date, IntegerUtil.getInt0(weekday));
		}


		if (month.matches(R1)) {
			// do nothing
		}
		if (month.matches(R2)) {
			String[] strs = month.split("/");
			_date = DateUtilBak.getNextMonthStep(_date, IntegerUtil.getInt0(strs[1]));
		}
		if (month.matches(R3)) {
			_date = DateUtilBak.getNextMonth(_date, IntegerUtil.getInt0(month));
		}


		if (day.matches(R1)) {
			// do nothing
		}
		if (day.matches(R2)) {
			String[] strs = day.split("/");
			_date = DateUtilBak.getNextDayStep(_date, IntegerUtil.getInt0(strs[1]));
		}
		if (day.matches(R3)) {
			_date = DateUtilBak.getNextDay(_date, IntegerUtil.getInt0(day));
		}


		if (hour.matches(R1)) {
			// do nothing
		}
		if (hour.matches(R2)) {
			String[] strs = hour.split("/");
			_date = DateUtilBak.getNextHourStep(_date, IntegerUtil.getInt0(strs[1]));
		}
		if (hour.matches(R3)) {
			_date = DateUtilBak.getNextHour(_date, IntegerUtil.getInt0(hour));
		}

		if (minute.matches(R1)) {
			// do nothing
			if (!hour.matches(R1)) {
				// 从第一分钟起
				_date = DateUtilBak.getNextMinute(date, _date, 1);
			} else {
				_date = DateUtilBak.getNextMinuteStep(_date, 1);
			}
		}
		if (minute.matches(R2)) {
			String[] strs = minute.split("/");
			_date = DateUtilBak.getNextMinuteStep(_date, IntegerUtil.getInt0(strs[1]));
		}
		if (minute.matches(R3)) {
			_date = DateUtilBak.getNextMinute(date, _date, IntegerUtil.getInt0(minute));
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
