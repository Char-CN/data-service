package org.blazer.test;

import java.sql.SQLException;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.Statement;
//
//import org.blazer.dataservice.dao.TransactionDao;
//
//import com.alibaba.druid.pool.DruidDataSource;
//
//import java.sql.DriverManager;

public class HiveJDBCTest {

//	private static String driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";

	public static void main(String[] args) throws SQLException {
//		try {
//			Class.forName(driverName);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//			System.exit(1);
//		}

//		DruidDataSource dataSource = new DruidDataSource();
//		dataSource.setUsername("hive");
//		dataSource.setPassword("hive");
//		dataSource.setUrl("jdbc:hive2://172.16.52.124:10000/default");
//		TransactionDao dao = new TransactionDao(dataSource);
//		String sql1 = "insert into jdbcTest values(1, 'bbbb')";
//		dao.update(sql1);

//		Connection con = DriverManager.getConnection("jdbc:hive2://172.16.52.124:10000/default", "hive", "hive");
//		Statement stmt = con.createStatement();
//		String tableName = "jdbcTest";
////		ResultSet res = stmt.executeQuery("insert into " + tableName + " values(1, 'bbbb')");
//		ResultSet res = stmt.executeQuery("select key, value from " + tableName);
//		if (res.next()) {
//			System.out.println(String.valueOf(res.getInt(1)) + "\t" + res.getString(2));
//		}
//		stmt.execute("drop table if exists " + tableName);
//		stmt.execute("create table " + tableName + " (key int, value string)");
//		System.out.println("Create table success!");
//		// show tables
//		String sql = "show tables '" + tableName + "'";
//		System.out.println("Running: " + sql);
//		ResultSet res = stmt.executeQuery(sql);
//		if (res.next()) {
//			System.out.println(res.getString(1));
//		}
//
//		// describe table
//		sql = "describe " + tableName;
//		System.out.println("Running: " + sql);
//		res = stmt.executeQuery(sql);
//		while (res.next()) {
//			System.out.println(res.getString(1) + "\t" + res.getString(2));
//		}
//
//		sql = "select * from " + tableName;
//		res = stmt.executeQuery(sql);
//		while (res.next()) {
//			System.out.println(String.valueOf(res.getInt(1)) + "\t" + res.getString(2));
//		}
//
//		sql = "select count(1) from " + tableName;
//		System.out.println("Running: " + sql);
//		res = stmt.executeQuery(sql);
//		while (res.next()) {
//			System.out.println(res.getString(1));
//		}
	}

}
