package org.blazer.scheduler.core;

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
		String str = "*/2 * * * *";
		System.out.println(check(str));

		String[] arr = toArray(str);
		for (String a : arr) {
			System.out.println(a);
		}

		TimeModel tm = DateUtil.now();
		System.out.println(tm);
		System.out.println(getNextDate(arr, tm));
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

	public static TimeModel getNextDate(String[] array, TimeModel tm) {
		// */2 * * * *
		String minute = array[0];
		String hour = array[1];
		String day = array[2];
		String month = array[3];
		String weekday = array[4];
		
		if (month.matches(R1)) {
			
		}
		
		if (minute.matches(R1)) {
			System.out.println("111");
		} else if (minute.matches(R2)) {
			int step = IntegerUtil.getInt0(minute.replace("*/", ""));
			System.out.println(222);
			System.out.println("now minute : " + tm.getMinute());
			System.out.println("step" + step);
		} else {
			System.out.println(333);
		}
		
		return null;
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
