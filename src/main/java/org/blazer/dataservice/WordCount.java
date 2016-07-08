package org.blazer.dataservice;

import java.util.HashMap;

public class WordCount {

	private static HashMap<String, Integer> map = new HashMap<String, Integer>();
	private static HashMap<String, Integer> map2 = new HashMap<String, Integer>();
	private static HashMap<String, Integer> map3 = new HashMap<String, Integer>();
	private static HashMap<String, Integer> mapLast10 = new HashMap<String, Integer>();
	static {
		for (int i = 0; i < 10; i++) {
			map.put("" + i, 0);
			mapLast10.put("" + i, 0);
		}
		map2.put("大", 0);
		map2.put("杀", 0);
		map2.put("小", 0);
		map2.put("和", 0);
		map3.put("🐲", 0);
		map3.put("🐯", 0);
		map3.put("🈴", 0);
	}

	public static void main(String[] args) {
		String str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "";
		str = "英皇走势，46668🐲大，06699🐲杀，22358🐲小，23357🐲小，12467🐲大，01225🐲小，01289🐲杀，35679🐲和，34445🐲和，22259🐲小，00145🐲小，35688🐲大，23447🐲小，15789🐲和，26688🐲大，13367🐯小，23799🐲小，02369🐲小，13358🐯大，22457🐲和，23889🐯小，12368🐲小，";
		str = "安安走势图。00488🐯大。00569🐲和。36777🈴️大。03368🐲大。12359🐯小。06789🐯大。02567🐯大。34779🐲小。13466🐲大。23889🐲大。00479🐯大。46677🐲大。";
		str = "安安走势图。00488🐯大。00569🐲和。36777🈴️大。03368🐲大。12359🐯小。06789🐯大。02567🐯大。34779🐲小。13466🐲大。23889🐲大。00479🐯大。46677🐲大。25788🐲和。01469🐯杀。15789🐲小。13466🈴️大。";


		String str2 = new StringBuffer(str).reverse().toString();
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			String val = str.substring(i, i+1);
			if (map.containsKey(val)) {
				map.put(val, map.get(val) + 1);
			}
			if (map2.containsKey(val)) {
				map2.put(val, map2.get(val) + 1);
				count++;
			}
			if (i == str.length() - 1) {
				break;
			}
			String val2 = val + str.substring(i+1, i+2);
			if (map3.containsKey(val2)) {
				map3.put(val2, map3.get(val2) + 1);
			}
		}
		int count2 = 0;
		for (int i = 0; i < str2.length() && count2 <= 10; i++) {
			String val = str2.substring(i, i+1);
			if (mapLast10.containsKey(val)) {
				mapLast10.put(val, mapLast10.get(val) + 1);
			}
			if (map2.containsKey(val)) {
				count2++;
			}
		}
		
		System.out.println("最近：" + (count) + "次");
		System.out.println("--------------------------------------------------");
		for (String s : map.keySet()) {
			System.out.println(s + ":" + map.get(s));
		}
		System.out.println("--------------------------------------------------");
		for (String s : map2.keySet()) {
			System.out.println(s + ":" + map2.get(s));
		}
		System.out.println("--------------------------------------------------");
		for (String s : map3.keySet()) {
			System.out.println(s + ":" + map3.get(s));
		}
		System.out.println("--------------------------------------------------");
		System.out.println("最近：" + (count2 - 1) + "次");
		System.out.println("--------------------------------------------------");
		for (String s : mapLast10.keySet()) {
			System.out.println(s + ":" + mapLast10.get(s));
		}
		System.out.println("--------------------------------------------------");
		
		// System.out.println(new java.util.Random().nextInt(10));
		// System.out.println(new java.util.Random().nextInt(10));
		
	}

}
