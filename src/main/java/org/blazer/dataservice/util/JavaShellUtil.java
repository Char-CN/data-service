package org.blazer.dataservice.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JavaShellUtil {

	public static final String basePath = "/Users/hyy/";

	public static final String shellFile = basePath + "zz.sh";

	public static void run(String cmd, String log, String errorLog) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Process process = Runtime.getRuntime().exec(cmd);
					log2File(log, process.getInputStream());
					log2File(errorLog, process.getErrorStream());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	public static void log2File(String path, InputStream is) throws IOException {
		System.out.println("new thread start : " + path);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File(path)), "utf-8");
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					String line = null;
					while (br != null && (line = br.readLine()) != null) {
						osw.write(System.currentTimeMillis() + "|" + line + "\n");
						osw.flush();
					}
					osw.close();
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}

	/**
	 * 合并读取文件
	 * 
	 * @param path1
	 * @param path2
	 * @throws Exception
	 */
	public static List<String> readLog(String path1, String path2) throws Exception {
		List<String> list = new ArrayList<String>();
		BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(path1), "UTF-8"));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(path2), "UTF-8"));
		String line1 = null;
		String line2 = null;
		while (true) {
			////////////////////////////////////////////////
			if (line1 == null && br1 != null) {
				line1 = br1.readLine();
			}
			if (line2 == null && br2 != null) {
				line2 = br2.readLine();
			}
			////////////////////////////////////////////////
			if (line1 != null && line2 != null) {
				// 对比
				Long l1 = 0L;
				if (line1.length() > 13) {
					l1 = Long.parseLong(line1.substring(0, 13));
				}
				Long l2 = 0L;
				if (line2.length() > 13) {
					l2 = Long.parseLong(line2.substring(0, 13));
				}
				if (l1 >= l2) {
					list.add(line2);
					line2 = null;
				} else {
					list.add(line1);
					line1 = null;
				}
			} else if (line1 != null) {
				list.add(line1);
				line1 = null;
			} else if (line2 != null) {
				list.add(line2);
				line2 = null;
			} else if (line1 == null && line2 == null) {
				break;
			}
		}
		br1.close();
		br2.close();
		return list;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(ManagementFactory.getRuntimeMXBean().getName());
		run("sh " + shellFile + " wwww sdsdsds aaasdasdaa", shellFile + ".input", shellFile + ".error");
		System.out.println(readLog(shellFile + ".input", shellFile + ".error"));
	}

}
