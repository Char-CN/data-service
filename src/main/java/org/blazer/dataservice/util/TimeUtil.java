package org.blazer.dataservice.util;

import org.slf4j.Logger;

/**
 * @author Blazer He
 * @date__ 2015年7月20日
 */
public class TimeUtil {

	private long point;

	private int step;

	private Logger logger = null;

	private static final String s = "秒";
	private static final String ms = "毫秒";
	private static final String m = "分钟";
	private static final String H = "小时";

	/**
	 * 获得一个TimeUtil
	 * 
	 * @return
	 */
	public static TimeUtil createAndPoint() {
		return new TimeUtil();
	}

	public TimeUtil setLogger(Logger logger) {
		this.logger = logger;
		return this;
	}

	private TimeUtil() {
		step = 0;
		point();
	}

	public long now() {
		return System.currentTimeMillis();
	}

	/**
	 * 埋点
	 */
	public TimeUtil point() {
		this.point = now();
		return this;
	}

	/**
	 * 计算毫秒
	 * 
	 * @return
	 */
	public long ms() {
		return getRstAndPointAndCalc(now());
	}

	public String msStr() {
		return getRstAndPointAndCalc(now()) + ms;
	}

	/**
	 * 计算秒
	 * 
	 * @return
	 */
	public long s() {
		return getRstAndPointAndCalc(now()) / 1000;
	}

	public String sStr() {
		return (getRstAndPointAndCalc(now()) / 1000) + s;
	}

	/**
	 * 计算分
	 * 
	 * @return
	 */
	public long m() {
		return getRstAndPointAndCalc(now()) / 60000;
	}

	public String mStr() {
		return (getRstAndPointAndCalc(now()) / 60000) + m;
	}

	/**
	 * 计算小时
	 * 
	 * @return
	 */
	public long H() {
		return getRstAndPointAndCalc(now()) / 3600000;
	}

	public String HStr() {
		return (getRstAndPointAndCalc(now()) / 3600000) + H;
	}

	/**
	 * 计算并且
	 * 
	 * @param now
	 * @return
	 */
	private long getRstAndPointAndCalc(long now) {
		long rst = now - point;
		point = now;
		step++;
		return rst;
	}

	public TimeUtil printMs(String message) {
		return print(outByStep(ms(), ms, message));
	}

	public TimeUtil printS(String message) {
		return print(outByStep(s(), s, message));
	}

	public TimeUtil printM(String message) {
		return print(outByStep(m(), m, message));
	}

	public TimeUtil printH(String message) {
		return print(outByStep(H(), H, message));
	}

	public TimeUtil printMs() {
		return print(outByStep(ms(), ms, ""));
	}

	public TimeUtil printS() {
		return print(outByStep(s(), s, ""));
	}

	public TimeUtil printM() {
		return print(outByStep(m(), m, ""));
	}

	public TimeUtil printH() {
		return print(outByStep(H(), H, ""));
	}

	private String outByStep(long time, String unit, String message) {
		return "第" + step + "步 [" + message + "] 消耗" + time + unit;
	}

	public TimeUtil printSWhere(int sTime, String message) {
		long tmpPoint = point;
		long diff = tmpPoint - now() / 1000;
		if (diff >= sTime) {
			point();
			print(outByTime(tmpPoint - point / 1000, s, message));
		}
		return this;
	}

	public String msgSWhere(int sTime, String message) {
		long tmpPoint = point;
		long diff = tmpPoint - now() / 1000;
		if (diff >= sTime) {
			point();
			return outByTime(point - tmpPoint / 1000, s, message);
		}
		return null;
	}

	public String msgMsWhere(int msTime, String message) {
		long tmpPoint = point;
		long diff = tmpPoint - now();
		if (diff >= msTime) {
			point();
			return outByTime(point - tmpPoint, ms, message);
		}
		return null;
	}

	private String outByTime(long diff, String unit, String message) {
		return "[" + message + "]消耗 " + diff + unit;
	}

	private TimeUtil print(String message) {
		if (logger != null) {
			logger.info(message);
		} else {
			System.out.println(message);
		}
		return this;
	}

}
