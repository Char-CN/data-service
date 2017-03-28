package org.blazer.dataservice.util;

import java.io.File;

public class FileUtil {

	public static String exists(String... paths) {
		for (String path : paths) {
			if (new File(path).exists()) {
				return path;
			}
		}
		return null;
	}

	public static String getFileName(String fileName) {
		int lastIndex = fileName.lastIndexOf(File.separator);
		if (lastIndex == -1) {
			return fileName;
		}
		return fileName.substring(lastIndex + 1);
	}
	
	public static void main(String[] args) {
		System.out.println(getFileName("/Users/hyy/scheduler_result/2017_03_27_16_25_24_cron_auto_00001.zip"));
	}

}
