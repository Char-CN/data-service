package org.blazer.scheduler.util;

import java.util.Calendar;

import org.blazer.scheduler.model.TimeModel;

public class DateUtil {

	public static final Integer monthStep = 1;
	public static final Integer dayStep = 1;
	public static final Integer hourStep = 1;
	public static final Integer minuteStep = 1;
	public static final Integer weekdayStep = 7;
	
	public static void main(String[] args) {
		System.out.println(now());
	}

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
