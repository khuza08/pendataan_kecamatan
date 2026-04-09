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

        if (username.isEmpty()) {
            messageLabel.setText("NIK atau Username diperlukan.");
            messageLabel.setStyle("-fx-text-fill: #2E261A;");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            // 1. If password is empty, try Warga-only login by NIK
            if (password.isEmpty()) {
                String wargaSql = "SELECT * FROM warga WHERE nik = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(wargaSql)) {
                    pstmt.setString(1, username);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        com.kecamatan.util.UserSession.loginWarga(rs.getInt("id"), rs.getString("nama"), rs.getString("nik"));
                        onLoginSuccess("WARGA");
                        return;
                    }
                }
                messageLabel.setText("NIK tidak ditemukan.");
                messageLabel.setStyle("-fx-text-fill: #2E261A;");
                return;
            }

            // 2. Both fields filled — try Admin Login
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

            messageLabel.setText("Username atau password salah.");
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
            App.preloadViews("dashboard", "desa", "kepala_desa", "warga", "laporan");
            App.setRoot("dashboard", 1200, 800, true);
        } else {
            App.preloadViews("profil", "kepala_desa_warga"); // Warga only needs Profil
            App.setRoot("profil", 1200, 800, true);
        }
    }
}
