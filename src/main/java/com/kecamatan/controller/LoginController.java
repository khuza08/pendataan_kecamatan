package com.kecamatan.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

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
            messageLabel.setStyle("-fx-text-fill: #2E261A;"); // Dark Brown from palette
        } else if ("admin".equals(username) && "admin123".equals(password)) {
            messageLabel.setText("Login successful!");
            messageLabel.setStyle("-fx-text-fill: #009C4B;"); // Green from palette
            // Add navigation logic here in the future
        } else {
            messageLabel.setText("Invalid credentials.");
            messageLabel.setStyle("-fx-text-fill: #2E261A;");
        }
    }
}
