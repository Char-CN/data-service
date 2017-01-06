package org.blazer.scheduler.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.blazer.scheduler.model.LogModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessHelper {

	private static final Logger logger = LoggerFactory.getLogger("scheduler");

	public static Process run(String cmd, String[] params, String logPath, String errorLogPath) {
		return run(cmd, params, logPath, errorLogPath, true);
	}

	public static Process run(String cmd, String[] params, String logPath, String errorLogPath, boolean appendLog) {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd, params);
			log2File(logPath, process.getInputStream(), appendLog);
			log2File(errorLogPath, process.getErrorStream(), appendLog);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return process;
	}

	public static void log2File(final String path, final String log, final boolean appendLog) throws IOException {
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

	public static LogModel readLog(String path1, String path2) throws Exception {
		return readLog(path1, path2, 0, 1000);
	}

	/**
	 * 合并读取文件
	 * 
	 * @param path1
	 * @param path2
	 * @throws Exception
	 */
	public static LogModel readLog(String path1, String path2, Integer skipRowNumber, Integer maxRowNumber) throws Exception {
		StringBuilder sb = new StringBuilder();
		BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(path1), "UTF-8"));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(path2), "UTF-8"));
		String line1 = null;
		String line2 = null;
		Integer count = 0;
		Integer skipCount = 0;
		while (true) {
			////////////////////////////////////////////////
			if (line1 == null && br1 != null) {
				line1 = br1.readLine();
			}
			if (line2 == null && br2 != null) {
				line2 = br2.readLine();
			}
			// 过滤的行数
			if (count < skipRowNumber) {
				count ++;
				skipCount ++;
				line1 = null;
				line2 = null;
				continue;
			}
			// 截止的行数
			if (count - skipCount >= maxRowNumber) {
				break;
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
//					list.add(line2);
					sb.append(line2.substring(14)).append("\n");
					line2 = null;
				} else {
//					list.add(line1);
					sb.append(line1.substring(14)).append("\n");
					line1 = null;
				}
			} else if (line1 != null) {
//				list.add(line1);
				sb.append(line1.substring(14)).append("\n");
				line1 = null;
			} else if (line2 != null) {
//				list.add(line2);
				sb.append(line2.substring(14)).append("\n");
				line2 = null;
			} else if (line1 == null && line2 == null) {
				break;
			}
			count ++;
		}
		br1.close();
		br2.close();
		LogModel lm = new LogModel();
		lm.setTotal(count);
		lm.setContent(sb.toString());
		return lm;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(readLog("/Users/hyy/test/2017_01_06_12_00_0_right_now_00002.log", "/Users/hyy/test/2017_01_06_12_00_0_right_now_00002.error", 0, 10).getContent());
		System.out.println(readLog("/Users/hyy/test/2017_01_06_12_00_0_right_now_00002.log", "/Users/hyy/test/2017_01_06_12_00_0_right_now_00002.error", 5, 5).getContent());
//		Map m = System.getenv();
//		for (Iterator it = m.keySet().iterator(); it.hasNext();) {
//			String key = (String) it.next();
//			String value = (String) m.get(key);
//			System.out.println(key + ":" + value);
//		}
//		Process process = null;
//		try {
//			process = Runtime.getRuntime().exec("sh /Users/hyy/test/sql.sh ${name}", new String[]{"SHELL=asd", "name=hyy"});
//			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			String line = null;
//			while (br != null && (line = br.readLine()) != null) {
//				System.out.println(line);
//			}
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//		}
		// String basePath = "/Users/hyy/";
		// String shellFile = basePath + "zz.sh";
		//
		// //
		// System.out.println(ManagementFactory.getRuntimeMXBean().getName());
		//
		// String cmd = ";;sleep ;echo
		// \"hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh............hhhhhhhhhhhhhhhhh\";sleep
		// 3s;echo `date +%Y%m%d%H%M%S`;sh " + shellFile + " wwww sdsdsds
		// aaasdasdaa;";
		// String[] cmds = StringUtils.split(cmd, ";");
		// System.out.println(cmds.length);
		// Process process = null;
		// for (String c : cmds) {
		// process = run(c, shellFile + ".input", shellFile + ".error");
		// System.out.println(c + " | " + process.waitFor());
		// }
		// // int i = process.waitFor();
		// // System.out.println(i);
		// // System.out.println(readLog(shellFile + ".input", shellFile +
		// // ".error"));
		// System.out.println("heheehee");
		// Thread.sleep(5000);
		// if (process.isAlive()) {
		// process.getOutputStream().close();
		// process.getInputStream().close();
		// process.getErrorStream().close();
		// process.destroy();
		// }
		// String cmd = "sh /Users/hyy/test/hyy.sh hhh";
		// String[] cmds = StringUtils.split(cmd, ";");
		// Process process = null;
		// String[] params = { "aaa=hyyyy" };
		// for (String c : cmds) {
		// process = run(c, params, "/Users/hyy/test/hyy.input",
		// "/Users/hyy/test/hyy.error", false);
		// System.out.println(c + " | " + process.waitFor());
		// }
		// System.out.println(process.exitValue());
		// String cmd = "sh /Users/hyy/test/sleep.sh";
		// String[] cmds = StringUtils.split(cmd, ";");
		// String[] params = { "aaa=hyyyy" };
		// Process process = run(cmd, params, "/Users/hyy/test/sleep.input",
		// "/Users/hyy/test/sleep.error");
		// Thread.sleep(12000);
		// process.destroy();
		// System.out.println(process.exitValue());
	}

}
