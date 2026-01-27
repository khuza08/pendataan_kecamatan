package com.kecamatan.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/pendataankecamatan";
    private static final String USER = "elza"; // Berdasarkan environment user
    private static final String PASSWORD = ""; // Asumsi tanpa password untuk user elza di local

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
