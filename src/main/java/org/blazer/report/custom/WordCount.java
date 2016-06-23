package org.blazer.report.custom;

import java.util.HashMap;

public class WordCount {

	private static HashMap<String, Integer> map = new HashMap<>();
	private static HashMap<String, Integer> map2 = new HashMap<>();
	static {
		for (int i = 0; i < 10; i++) {
			map.put("" + i, 0);
		}
		map2.put("大", 0);
		map2.put("杀", 0);
		map2.put("小", 0);
		map2.put("和", 0);
	}

	public static void main(String[] args) {
		String str = "安安走势图。。45678🐯大，01559🐯大，03359🐯杀，01478🐯杀，01388🐯大，01568🐲大，11477🐲大，02459🐲小，03566🐯和，11477🐯大，12566🐯小，12368🐲小，23456🐯大，03467🐲小，13349🈴️小，45678🐯大，13367🐯小，01234🐯小，00226🐯小，01568🐲和，13466🐲大，01379🐲杀，35679🐲大，01126🐲大，-";

		int i = 0;
		for (char c : str.toCharArray()) {
			if (map.containsKey("" + c)) {
				map.put("" + c, map.get("" + c) + 1);
				i++;
			}
			if (map2.containsKey("" + c)) {
				map2.put("" + c, map2.get("" + c) + 1);
				i++;
			}
		}

		System.out.println("一共开：" + (i / 5) + "次");
		for (String s : map.keySet()) {
			System.out.println(s + ":" + map.get(s));
		}
		System.out.println("--------------------------------------------------");
		for (String s : map2.keySet()) {
			System.out.println(s + ":" + map2.get(s));
		}
	}

}
