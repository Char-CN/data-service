package org.blazer.dataservice.util;

public class SqlUtil {

	public static String TransactSQLInjection(String sql) {
		return sql.replaceAll(".*([';]+|(--)+).*", " ");
	}

}
