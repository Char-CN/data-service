package org.blazer.dataservice.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 8725758883086149921L;

	public <T> T to(Class<T> cls) throws Exception {
		return to(this, cls);
	}

	public static <T> T to(@SuppressWarnings("rawtypes") Map map, Class<T> cls) throws Exception {
		T rst = cls.newInstance();
		for (Field f : cls.getDeclaredFields()) {
			f.setAccessible(true);
			try {
				Object value = null;
				if (map.get(f.getName()) != null) {
					value = map.get(f.getName());
				} else if (map.get(f.getName().replaceAll("[A-Z]", "_$0").toLowerCase()) != null) {
					value = map.get(f.getName().replaceAll("[A-Z]", "_$0").toLowerCase());
				}
				if (f.getGenericType().toString().equals("class java.lang.Integer")) {
					f.set(rst, IntegerUtil.getInt(value));
				} else {
					f.set(rst, value);
				}
			} catch (Exception e) {
				f.set(rst, null);
			}
		}
		return rst;
	}

	@SuppressWarnings("rawtypes")
	public static <T> T to(Object obj, Class<T> cls) throws Exception {
		return to((Map) obj, cls);
	}

	public static <T> List<T> toList(List<Map<String, Object>> src, Class<T> cls) throws Exception {
		List<T> list = new ArrayList<T>();
		for (Map<String, Object> map : src) {
			T t = to(map, cls);
			list.add(t);
		}
		return list;
	}

}
