package org.blazer.dataservice.util;

public class IntegerUtil {

	public static Integer getInt(Object o) {
		if (o == null) {
			return null;
		}
		try {
			return Integer.parseInt(o.toString());
		} catch (Exception e) {
		}
		return null;
	}

	public static Integer getInt0(Object o) {
		if (o == null) {
			return 0;
		}
		try {
			return Integer.parseInt(o.toString());
		} catch (Exception e) {
		}
		return 0;
	}
	
	public static Integer getInt1(Object o) {
		if (o == null) {
			return 1;
		}
		try {
			return Integer.parseInt(o.toString());
		} catch (Exception e) {
		}
		return 1;
	}

}
