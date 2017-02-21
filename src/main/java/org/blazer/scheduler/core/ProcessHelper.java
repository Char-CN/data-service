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
import org.blazer.scheduler.model.LogModel;
import org.blazer.scheduler.model.ResultModel;
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
	 * 读取单个文件
	 * 
	 * @return
	 * @throws Exception
	 */
	public static ResultModel readSingleLog(String path1, Integer skipRowNumber, Integer maxRowNumber) throws Exception {
		List<String[]> result = new ArrayList<String[]>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path1), "UTF-8"));
		String line = null;
		Integer count = 0;
		Integer skipCount = 0;
		boolean complete = true;
		while ((line = br.readLine()) != null) {
			// 过滤的行数
			if (count < skipRowNumber) {
				count++;
				skipCount++;
				continue;
			}
			// 截止的行数
			if (count - skipCount >= maxRowNumber) {
				complete = false;
				break;
			}
			result.add(StringUtils.splitByWholeSeparatorPreserveAllTokens(line, "\t"));
			count++;
		}
		br.close();
		ResultModel rm = new ResultModel();
		rm.setTotal(count);
		rm.setResult(result);
		rm.setComplete(complete);
		return rm;
	}

	static class Row {
		String line1;
		String line2;
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

		Row row = new ProcessHelper.Row();
		row.line1 = null;
		row.line2 = null;
		Integer count = 0;
		// Integer skipCount = 0;
		while (true) {
			////////////////////////////////////////////////
			if (row.line1 == null && br1 != null) {
				row.line1 = br1.readLine();
			}
			if (row.line2 == null && br2 != null) {
				row.line2 = br2.readLine();
			}
			// 过滤的行数
			if (count < skipRowNumber) {
				count++;
				// 比较是输出哪一个，但不实际输出，只做过滤。
				compareOutput(row);
				continue;
			}
			// 截止的行数
			if (count - skipRowNumber >= maxRowNumber) {
				break;
			}
			////////////////////////////////////////////////
			String out = compareOutput(row);
			// 如果为null则表示都没有需要输出的内容，则结束循环。
			if (out == null) {
				break;
			}
			// 否则输出
			else {
				sb.append(out);
			}
			count++;
		}
		br1.close();
		br2.close();
		LogModel lm = new LogModel();
		lm.setTotal(count);
		lm.setContent(sb.toString());
		return lm;
	}

	/**
	 * 比较log和error中的各一行，然后输出。
	 * @param row
	 * @return
	 */
	private static String compareOutput(Row row) {
		String rt = null;
		if (row.line1 != null && row.line2 != null) {
			// 对比
			Long l1 = 0L;
			try {
				l1 = Long.parseLong(row.line1.substring(0, 13));
			} catch (Exception e) {
				logger.error("log原始行：" + row.line1);
				logger.error("错误信息：" + e.getMessage());
			}
			Long l2 = 0L;
			try {
				l2 = Long.parseLong(row.line2.substring(0, 13));
			} catch (Exception e) {
				logger.error("error原始行：" + row.line2);
				logger.error("错误信息：" + e.getMessage());
			}
			if (l1 == 0L) {
//				rt = row.line1.substring(14) + "\n";
				row.line1 = null;
			} else if (l2 == 0L) {
//				rt = row.line2.substring(14) + "\n";
				row.line2 = null;
			} else if (l1 >= l2) {
				// list.add(line2);
				rt = row.line2.substring(14) + "\n";
				row.line2 = null;
//				sb.append(line2.substring(14)).append("\n");
			} else {
				// list.add(line1);
				rt = row.line1.substring(14) + "\n";
				row.line1 = null;
//				sb.append(line1.substring(14)).append("\n");
			}
		} else if (row.line1 != null) {
			// list.add(line1);
			rt = row.line1.substring(14) + "\n";
			row.line1 = null;
//			sb.append(line1.substring(14)).append("\n");
		} else if (row.line2 != null) {
			// list.add(line2);
			rt = row.line2.substring(14) + "\n";
			row.line2 = null;
//			sb.append(line2.substring(14)).append("\n");
		} else if (row.line1 == null && row.line2 == null) {
			rt = null;
		}
		return rt;
	}

	public static void main(String[] args) throws Exception {
//		System.out.println(
//				readLog("/Users/hyy/test/2017_01_06_12_00_0_right_now_00002.log", "/Users/hyy/test/2017_01_06_12_00_0_right_now_00002.error", 0, 200).getTotal());
//		System.out.println("================================================");
		System.out.println(
				readLog("/Users/hyy/test/2017_01_06_12_00_0_right_now_00002.log", "/Users/hyy/test/2017_01_06_12_00_0_right_now_00002.error", 40, 5).toString());
		// Map m = System.getenv();
		// for (Iterator it = m.keySet().iterator(); it.hasNext();) {
		// String key = (String) it.next();
		// String value = (String) m.get(key);
		// System.out.println(key + ":" + value);
		// }
		// Process process = null;
		// try {
		// process = Runtime.getRuntime().exec("sh /Users/hyy/test/sql.sh
		// ${name}", new String[]{"SHELL=asd", "name=hyy"});
		// BufferedReader br = new BufferedReader(new
		// InputStreamReader(process.getInputStream()));
		// String line = null;
		// while (br != null && (line = br.readLine()) != null) {
		// System.out.println(line);
		// }
		// } catch (Exception e) {
		// logger.error(e.getMessage(), e);
		// }
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
