package org.blazer.dataservice.util;

public class LongUtil {

	public static Long getLong(Object o) {
		if (o == null) {
			return null;
		}
		try {
			return Long.parseLong(o.toString());
		} catch (Exception e) {
		}
		return null;
	}

	public static Long getLong0(Object o) {
		if (o == null) {
			return 0L;
		}
		try {
			return Long.parseLong(o.toString());
		} catch (Exception e) {
		}
		return 0L;
	}

	public static Long getLong1(Object o) {
		if (o == null) {
			return 1L;
		}
		try {
			return Long.parseLong(o.toString());
		} catch (Exception e) {
		}
		return 1L;
	}

}
