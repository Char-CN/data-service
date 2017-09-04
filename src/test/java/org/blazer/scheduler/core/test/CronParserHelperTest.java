package org.blazer.scheduler.core.test;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.blazer.scheduler.core.CronParserHelper;
import org.blazer.scheduler.expression.CronCalcTimeoutException;
import org.blazer.scheduler.expression.CronException;
import org.blazer.scheduler.util.DateUtil;
import org.junit.Test;

public class CronParserHelperTest {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Test
	public void evaluatesExpression() throws CronException, CronCalcTimeoutException {
		String cron = null;
		Date next = null;
		long l1 = 0;
		long l2 = 0;

		cron = "* * * * *";
		System.out.println(cron + "  : " + CronParserHelper.isValid(cron));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(DateUtil.newDate(), cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time   : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time   : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time   : " + sdf.format(next));
		System.out.println();

		cron = "20,30,40 * * * *";
		System.out.println(cron + " : " + CronParserHelper.isValid(cron));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(DateUtil.newDate(), cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		System.out.println();

		cron = "*/5 * * * */2";
		System.out.println(cron + " : " + CronParserHelper.isValid(cron));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(DateUtil.newDate(), cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		System.out.println();

		cron = "20 * * * *";
		System.out.println(cron + " : " + CronParserHelper.isValid(cron));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(DateUtil.newDate(), cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		System.out.println();

		cron = "15-16 * * * *";
		System.out.println(cron + " : " + CronParserHelper.isValid(cron));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(DateUtil.newDate(), cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		System.out.println();

		cron = "0 23 * * 6";
		System.out.println(cron + "  : " + CronParserHelper.isValid(cron));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(DateUtil.newDate(), cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time   : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time   : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time   : " + sdf.format(next));
		System.out.println();

		cron = "1 1 1 1 0";
		System.out.println(cron + " : " + CronParserHelper.isValid(cron));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(DateUtil.newDate(), cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time : " + sdf.format(next));
		System.out.println();

		cron = "1 1 10,11,12 */3 2-4";
		System.out.println(cron + "  : " + CronParserHelper.isValid(cron));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(DateUtil.newDate(), cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time   : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time   : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time   : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time   : " + sdf.format(next));
		l1 = System.currentTimeMillis();
		next = CronParserHelper.getNextDate(next, cron);
		l2 = System.currentTimeMillis();
		System.out.println("waster time : " + (l2 - l1) + "ms");
		System.out.println("next time   : " + sdf.format(next));
		System.out.println();
	}

}
