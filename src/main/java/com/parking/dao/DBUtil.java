package com.parking.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

	private static final String URL =
	        "jdbc:mysql://localhost:3306/parking_db?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";       // 내 MySQL 계정
    private static final String PASSWORD = "0000";   // 내가 설정한 비밀번호

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // MySQL 8용 드라이버
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
