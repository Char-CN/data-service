package org.blazer.scheduler.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.blazer.scheduler.util.DateUtil;

public class TimeModel {

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) {
		TimeModel tm = new TimeModel(2016, 12, 9, 15, 32, 33);
		System.out.println(tm);
		System.out.println(DateUtil.now());
	}

	private Date date;

	public TimeModel(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer seconds) {
		final Calendar c = Calendar.getInstance();
		c.set(year, month - 1, day, hour, minute, seconds);
		this.date = c.getTime();
		System.out.println();
	}
	
	public Integer getMinute(){
		final Calendar c = Calendar.getInstance();
		c.setTime(this.date);
		return c.get(Calendar.MINUTE);
	}
	
	@Override
	public String toString() {
		return sdf.format(date);
	}

}
