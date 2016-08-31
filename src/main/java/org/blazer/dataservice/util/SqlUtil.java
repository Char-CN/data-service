package org.blazer.dataservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlUtil {

	public static String TransactSQLInjection(String sql) {
		return sql.replaceAll(".*([';]+|(--)+).*", " ");
	}

	public static List<String> ExtractParams(String sql) {
		Pattern p = Pattern.compile("[$][{][a-zA-Z0-9:._-]*[}]");
		Matcher m = p.matcher(sql);
		List<String> result = new ArrayList<String>();
		while (m.find()) {
			result.add(m.group());
		}
		return result;
	}

	public static String Show(String sql, Object... objs) {
		for (Object obj : objs) {
			sql = sql.replaceFirst("[?]", StringUtil.getStr(obj));
		}
		return sql;
	}

	public static void main(String[] args) {
		System.out.println(SqlUtil.TransactSQLInjection("asd';select * from a; '"));
		System.out.println(SqlUtil.Show("select * from aaa limit ?,?", 1, "2"));
		System.out.println(ExtractParams("select * from table where a='${hello}' and b='${hyy}'"));
		System.out.println(ExtractParams("select * from table where a='${aaa}' and b='${hyy}'"));
	}

}
