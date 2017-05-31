package org.blazer.dataservice.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.blazer.dataservice.action.SystemAction;
import org.blazer.dataservice.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class DimensionInitUtil {

	private static Logger logger = LoggerFactory.getLogger(DimensionInitUtil.class);

	private static SimpleDateFormat yyyySDF = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat yyyy_SDF = new SimpleDateFormat("yyyy-");
	private static SimpleDateFormat yyyyMMSDF = new SimpleDateFormat("yyyyMM");
	private static SimpleDateFormat yyyy_MMSDF = new SimpleDateFormat("yyyy-MM");
	private static SimpleDateFormat yyyyMMddSDF = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat yyyy_MM_ddSDF = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat yyyyMMddHHSDF = new SimpleDateFormat("yyyyMMddHH");
	private static SimpleDateFormat yyyy_MM_dd_HHSDF = new SimpleDateFormat("yyyy-MM-dd HH");
	private static SimpleDateFormat yyyyMMddHHmmSDF = new SimpleDateFormat("yyyyMMddHHmm");
	private static SimpleDateFormat yyyy_MM_dd_HH_mmSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	public static void init(String firstDay, String dayCount, Dao dao, String tableName) throws ParseException {
		// 秒、分、时、日、周、月、季、年、总
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(yyyyMMddSDF.parse(firstDay));
		logger.info(yyyy_MM_ddSDF.format(calendar.getTime()));
		// 每一年
		String year = "";
		// 每一月
		String month = "";
		// 每一天
		for (int i = 0; i < Integer.parseInt(dayCount); i++) {
			// 年
			String yearTmp = yyyySDF.format(calendar.getTime());
			if (!year.equals(yearTmp)) {
				year = yearTmp;
				execute(year, year, dao, tableName);
			}
			// 年-月
			String monthTmp = yyyyMMSDF.format(calendar.getTime());
			if (!month.equals(monthTmp)) {
				month = monthTmp;
				execute(monthTmp, yyyy_MMSDF.format(yyyyMMSDF.parse(monthTmp)), dao, tableName);
			}
			// 年-月-日
			String yyyyMMdd = yyyyMMddSDF.format(calendar.getTime());
			String yyyy_MM_dd = yyyy_MM_ddSDF.format(calendar.getTime());
			execute(yyyyMMdd, yyyy_MM_dd, dao, tableName);

			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
		}
		// 重置时间
		// 分
		calendar.setTime(yyyyMMddSDF.parse(firstDay));
		logger.info(yyyy_MM_ddSDF.format(calendar.getTime()));

		String hour = "";
		for (int i = 0; i < Integer.parseInt(dayCount) * 24 * 60; i++) {
			// 年-月-日 时
			String hourTmp = yyyyMMddHHSDF.format(calendar.getTime());
			if (!hour.equals(hourTmp)) {
				hour = hourTmp;
				execute(hour, yyyy_MM_dd_HHSDF.format(calendar.getTime()), dao, tableName);
			}
			// 年-月-日 时:分
			// String yyyyMMddHHmm = yyyyMMddHHmmSDF.format(calendar.getTime());
			// String yyyy_MM_dd_HH_mm =
			// yyyy_MM_dd_HH_mmSDF.format(calendar.getTime());
			// execute(yyyyMMddHHmm, yyyy_MM_dd_HH_mm);
			calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
		}
		logger.info("一共[" + count + "]条记录");
		logger.info("截至时间 " + yyyy_MM_dd_HH_mmSDF.format(calendar.getTime()));
	}

	private static int count = 0;

	private static void execute(String str1, String str2, Dao dao, String tableName) {
		String upSql = "insert into " + tableName + " values('" + str1 + "', '" + str2 + "') on duplicate key update time_key=values(time_key)";
		dao.update(upSql);
		count++;
		if (count % 100 == 0) {
			logger.info("insert[" + count + "]");
		}
	}

}
