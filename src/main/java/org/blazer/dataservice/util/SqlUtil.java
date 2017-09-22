package org.blazer.dataservice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlUtil {

	/**
	 * 防止SQL注入, 将替换括号之内的内容:.*([';]+|(--)+).*
	 * 
	 * @param sql
	 * @return
	 */
	public static String TransactSQLInjection(String sql) {
		return sql.replaceAll(".*([';]+|(--)+).*", " ");
	}

	/**
	 * 抓取参数，如sql中包含${param}，则返回一个抓取出来参数的List
	 * 
	 * @param sql
	 * @return
	 */
	public static List<String> ExtractParams(String sql) {
		Pattern p = Pattern.compile("[$][{][\u4e00-\u9fa5a-zA-Z0-9:._-]*[}]");
		Matcher m = p.matcher(sql);
		List<String> result = new ArrayList<String>();
		while (m.find()) {
			result.add(m.group());
		}
		return result;
	}

	/**
	 * 打印实际sql，将?转换成参数
	 * 
	 * @param sql
	 * @param objs
	 * @return
	 */
	public static String Show(String sql, Object... objs) {
		for (Object obj : objs) {
			sql = sql.replaceFirst("[?]", "'" + StringUtil.getStr(obj) + "'");
		}
		return sql;
	}

	/**
	 * 支持三种注释:
	 * 
	 * # 注释
	 * 
	 * -- 注释
	 * 
	 * /* 注释 * /
	 * 
	 * @param sql字符串
	 * @return
	 */
	public static String removeComment(String sql) {
		// 拼接字符串
		StringBuilder sb = new StringBuilder();
		// 注释一:#
		boolean inCommentA = false;
		// 注释二:--
		boolean inCommentB = false;
		boolean beginCommentB = false;
		// 注释三:/* */
		boolean inCommentC = false;
		boolean beginCommentC = false;
		boolean endCommentC = false;
		for (int i = 0; i < sql.length(); i++) {
			char c = sql.charAt(i);
			// 遇见换行
			if (c == '\r' || c == '\n') {
				if (inCommentA) {
					inCommentA = false;
				} else if (inCommentB) {
					inCommentB = false;
				} else {
					sb.append(c);
				}
				continue;
			}
			// 如果正在注释一内
			if (inCommentA) {
				continue;
			}
			// 如果正在注释二内
			if (inCommentB) {
				continue;
			}
			// 如果正在注释三内
			if (inCommentC) {
				if (c == '*') {
					endCommentC = true;
				} else if (c == '/') {
					if (endCommentC) {
						inCommentC = false;
					}
				}
				continue;
			}
			// 遇见注释一
			if (c == '#') {
				inCommentA = true;
				continue;
			}
			// 遇见注释二
			else if (c == '-') {
				// 上一个是-符号
				if (beginCommentB) {
					// 开启注释二
					inCommentB = true;
					beginCommentB = false;
					sb.setLength(sb.length() - 1);
					continue;
				}
				// 上一个不是-符号
				else {
					beginCommentB = true;
				}
			}
			// 遇见注释三
			else if (c == '/') {
				beginCommentC = true;
			}
			// 遇见注释三
			else if (c == '*') {
				if (beginCommentC) {
					inCommentC = true;
					beginCommentC = false;
					sb.setLength(sb.length() - 1);
					continue;
				}
			}
			// 否则取消注释
			else {
				beginCommentB = false;
			}
			sb.append(c);
		}
		return sb.toString();
	}

	/**
	 * 根据sql中的;符号拆分sql，返回string数组
	 * 
	 * @param sql
	 * @return
	 */
	public static List<String> splitSql(String sql) {
		List<String> list = new ArrayList<String>();
		// 先获得每个正确;符号的位置
		List<Integer> splitList = new ArrayList<Integer>();
		boolean inString = false;
		for (int i = 0; i < sql.length(); i++) {
			char c = sql.charAt(i);
			// 遇见 单引号
			if (c == '\'') {
				if (inString) {
				}
			}
		}

		list.add("asd");
		return null;
	}

	public static void main(String[] args) {
		System.out.println(SqlUtil.TransactSQLInjection("asd';select * from a; '"));
		System.out.println(SqlUtil.Show("select * from aaa limit ?,?", 1, "2"));
		System.out.println(ExtractParams("select * from table where a='${hello}' and b='${hyy}'"));
		System.out.println(ExtractParams("select * from table where a='${aaa}' and b='${hyy}'"));
		String sql = "select */* asdas */ from table where 1=1 # hello \n \n";
		sql += "########asdasd  \n";
		sql += "-- hello \n";
		sql += "-----##asdasd \n";
		sql += "and /* here */ a='${aaa};' \n ";
		sql += "and b='${hyy}';";
		System.out.println(removeComment(sql));
	}

}
