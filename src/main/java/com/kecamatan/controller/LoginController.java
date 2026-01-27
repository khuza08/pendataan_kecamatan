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
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                messageLabel.setText("Login successful!");
                messageLabel.setStyle("-fx-text-fill: #009C4B;");
                
                // Navigate to dashboard
                App.setRoot("dashboard", 800, 600);
            } else {
                messageLabel.setText("Invalid credentials.");
                messageLabel.setStyle("-fx-text-fill: #2E261A;");
            }
        } catch (SQLException | IOException e) {
            messageLabel.setText("Database error: " + e.getMessage());
            messageLabel.setStyle("-fx-text-fill: #2E261A;");
            e.printStackTrace();
        }
    }
}
