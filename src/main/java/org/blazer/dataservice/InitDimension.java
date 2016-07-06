package org.blazer.dataservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.druid.pool.DruidDataSource;

@SuppressWarnings("unused")
public class InitDimension {

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

	static JdbcTemplate jdbcTemplate;

	static {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUsername("dev");
		dataSource.setPassword("dev123456");
		dataSource.setUrl("jdbc:mysql://ms:3306/dw_realtime?characterEncoding=utf8&useSSL=false");
		dataSource.setMaxActive(100);
		dataSource.setPoolPreparedStatements(true);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(100);
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public static void main(String[] args) throws ParseException {
		String firstDay = "20160101";
		String dayCount = "1000";
		if (args.length >= 1) {
			firstDay = args[0];
		}
		if (args.length >= 2) {
			dayCount = args[1];
		}
		// 秒、分、时、日、周、月、季、年、总
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(yyyyMMddSDF.parse(firstDay));
		System.out.println(yyyy_MM_ddSDF.format(calendar.getTime()));
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
				execute(year, year);
			}
			// 年-月
			String monthTmp = yyyyMMSDF.format(calendar.getTime());
			if (!month.equals(monthTmp)) {
				month = monthTmp;
				execute(monthTmp, yyyy_MMSDF.format(yyyyMMSDF.parse(monthTmp)));
			}
			// 年-月-日
			String yyyyMMdd = yyyyMMddSDF.format(calendar.getTime());
			String yyyy_MM_dd = yyyy_MM_ddSDF.format(calendar.getTime());
			execute(yyyyMMdd, yyyy_MM_dd);

			calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
		}
		// 重置时间
		// 分
		calendar.setTime(yyyyMMddSDF.parse(firstDay));
		System.out.println(yyyy_MM_ddSDF.format(calendar.getTime()));

		String hour = "";
		for (int i = 0; i < Integer.parseInt(dayCount) * 24 * 60; i++) {
			// 年-月-日 时
			String hourTmp = yyyyMMddHHSDF.format(calendar.getTime());
			if (!hour.equals(hourTmp)) {
				hour = hourTmp;
				execute(hour, yyyy_MM_dd_HHSDF.format(calendar.getTime()));
			}
			// 年-月-日 时:分
//			String yyyyMMddHHmm = yyyyMMddHHmmSDF.format(calendar.getTime());
//			String yyyy_MM_dd_HH_mm = yyyy_MM_dd_HH_mmSDF.format(calendar.getTime());
//			execute(yyyyMMddHHmm, yyyy_MM_dd_HH_mm);
			calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);
		}
		System.out.println("一共[" + count + "]条记录");
		System.out.println("截至时间 " + yyyy_MM_dd_HH_mmSDF.format(calendar.getTime()));
	}

	private static int count = 0;

	private static void execute(String str1, String str2) {
		String upSql = "insert into dw_realtime.rt_time values('" + str1 + "', '" + str2 + "') on duplicate key update time_key=values(time_key)";
//		System.out.println(upSql);
		jdbcTemplate.execute(upSql);
		count++;
		if (count % 100 == 0) {
			System.out.println(count);
		}
	}

}
