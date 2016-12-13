package org.blazer.scheduler.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ExecutorUtil {

	public static Process run(String cmd, String logPath, String errorLogPath) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			log2File(logPath, process.getInputStream());
			log2File(errorLogPath, process.getErrorStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return process;
	}

	public static void log2File(String path, InputStream is) throws IOException {
		System.out.println("new thread start : " + path);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				OutputStreamWriter osw = null;
				BufferedReader br = null;
				try {
					osw = new OutputStreamWriter(new FileOutputStream(new File(path)), "utf-8");
					br = new BufferedReader(new InputStreamReader(is));
					String line = null;
					while (br != null && (line = br.readLine()) != null) {
						osw.write(System.currentTimeMillis() + "|" + line + "\n");
						osw.flush();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						if (osw != null) {
							osw.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						if (br != null) {
							br.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
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
		String basePath = "/Users/hyy/";
		String shellFile = basePath + "zz.sh";

		// System.out.println(ManagementFactory.getRuntimeMXBean().getName());
		Process process = run("sh " + shellFile + " wwww sdsdsds aaasdasdaa", shellFile + ".input", shellFile + ".error");
		// int i = process.waitFor();
		// System.out.println(i);
		// System.out.println(readLog(shellFile + ".input", shellFile +
		// ".error"));
		System.out.println("heheehee");
		Thread.sleep(5000);
		if (process.isAlive()) {
			process.getOutputStream().close();
			process.getInputStream().close();
			process.getErrorStream().close();
			process.destroy();
		}
	}

}
