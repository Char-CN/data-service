package org.blazer.dataservice.util;

public class BooleanUtil {

	public static boolean getBool(Object o) {
		if (o == null) {
			return false;
		}
		try {
			if ("1".equals(o.toString())) {
				return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	public static void main(String[] args) {
		System.out.println(getBool(null));
		System.out.println(getBool("0"));
		System.out.println(getBool("1"));
	}

}
