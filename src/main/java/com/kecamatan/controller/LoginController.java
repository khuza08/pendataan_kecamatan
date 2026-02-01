package com.kecamatan.controller;

import com.kecamatan.App;
import com.kecamatan.util.DatabaseUtil;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Username and password are required.");
            messageLabel.setStyle("-fx-text-fill: #2E261A;");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            // 1. Try Admin Login
            String adminSql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(adminSql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    com.kecamatan.util.UserSession.login(rs.getInt("id"), rs.getString("username"), "ADMIN");
                    onLoginSuccess("ADMIN");
                    return;
                }
            }

            // 2. Try Warga Login (Username = NIK, Password = Tgl Lahir dd-mm-yyyy)
            String wargaSql = "SELECT * FROM warga WHERE nik = ? AND tanggal_lahir = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(wargaSql)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    com.kecamatan.util.UserSession.loginWarga(rs.getInt("id"), rs.getString("nama"), rs.getString("nik"));
                    onLoginSuccess("WARGA");
                    return;
                }
            }

            messageLabel.setText("Invalid credentials.");
            messageLabel.setStyle("-fx-text-fill: #2E261A;");
            
        } catch (SQLException | IOException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: #2E261A;");
            e.printStackTrace();
        }
    }

    private void onLoginSuccess(String role) throws IOException {
        messageLabel.setText("Login successful!");
        messageLabel.setStyle("-fx-text-fill: #009C4B;");
        
        if ("ADMIN".equals(role)) {
            App.preloadViews("dashboard", "kecamatan", "desa", "warga", "laporan");
        } else {
            App.preloadViews("dashboard"); // Warga sees limited views
        }
        
        App.setRoot("dashboard", 1200, 800, true);
    }
}
