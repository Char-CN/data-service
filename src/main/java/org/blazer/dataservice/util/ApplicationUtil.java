package org.blazer.dataservice.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class ApplicationUtil {

	private static ApplicationContext ctx = null;

	public static void init() {
		long l1 = System.currentTimeMillis();
		if (ctx != null) {
			long l2 = System.currentTimeMillis();
			System.out.println("init spring applicationContext.xml waste time : " + (l2 - l1));
			return;
		}
		try {
			String applicationPath = "src/main/resources/applicationContext.xml";
			ApplicationUtil.ctx = new FileSystemXmlApplicationContext(applicationPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long l2 = System.currentTimeMillis();
		System.out.println("init spring applicationContext.xml waste time : " + (l2 - l1));
	}

	public static void setCtx(ApplicationContext ctx) {
		ApplicationUtil.ctx = ctx;
	}

	public static Object getBean(String beanName) {
		return ctx.getBean(beanName);
	}

}
