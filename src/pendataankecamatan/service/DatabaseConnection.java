// src/pendataankecamatan/service/DatabaseConnection.java
package pendataankecamatan.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/pendataankecamatan?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USERNAME = "root";       
    private static final String PASSWORD = "root";           

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "MySQL JDBC Driver tidak ditemukan!\nPastikan mysql-connector-j sudah ditambahkan ke Libraries.",
                "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Gagal memuat driver MySQL", e);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal terhubung ke database:\n" + e.getMessage(),
                "Koneksi Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }
}