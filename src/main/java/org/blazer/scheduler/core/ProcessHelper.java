package org.blazer.scheduler.core;

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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessHelper {

	private static final Logger logger = LoggerFactory.getLogger("scheduler");

	public static Process run(String cmd, String logPath, String errorLogPath) {
		return run(cmd, logPath, errorLogPath, true);
	}

	public static Process run(String cmd, String logPath, String errorLogPath, boolean appendLog) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd);
			log2File(logPath, process.getInputStream(), appendLog);
			log2File(errorLogPath, process.getErrorStream(), appendLog);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return process;
	}

	public static void log2File(final String path, final String log, boolean appendLog) throws IOException {
		logger.debug("new thread start : " + path);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				OutputStreamWriter osw = null;
				try {
					osw = new OutputStreamWriter(new FileOutputStream(new File(path), appendLog), "utf-8");
					String[] logs = log.split("\n");
					for (String line : logs) {
						osw.write(System.currentTimeMillis() + "|" + line + "\n");
					}
					osw.flush();
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
				}
			}
		});
		t.start();
	}

	public static void log2File(final String path, final InputStream is, final boolean appendLog) throws IOException {
		logger.debug("new thread start : " + path);
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				OutputStreamWriter osw = null;
				BufferedReader br = null;
				try {
					osw = new OutputStreamWriter(new FileOutputStream(new File(path), appendLog), "utf-8");
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

		String cmd = ";;sleep ;echo \"hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh............hhhhhhhhhhhhhhhhh\";sleep 3s;echo `date +%Y%m%d%H%M%S`;sh " + shellFile + " wwww sdsdsds aaasdasdaa;";
		String[] cmds = StringUtils.split(cmd, ";");
		System.out.println(cmds.length);
		Process process = null;
		for (String c : cmds) {
			process = run(c, shellFile + ".input", shellFile + ".error");
			System.out.println(c + " | " + process.waitFor());
		}
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
