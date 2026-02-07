package com.kecamatan.util;

import com.kecamatan.App;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import javafx.scene.Node;

public class UIUtil {

    public static void setErrorStyle(Node node, boolean error) {
        if (node == null) return;
        if (error) {
            if (!node.getStyleClass().contains("input-error")) {
                node.getStyleClass().add("input-error");
            }
        } else {
            node.getStyleClass().remove("input-error");
        }
    }

    public static void showAlert(String title, String content, Alert.AlertType type) {
        if (Platform.isFxApplicationThread()) {
            performShowAlert(title, content, type);
        } else {
            Platform.runLater(() -> performShowAlert(title, content, type));
        }
    }

    private static void performShowAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        Stage primary = App.getPrimaryStage();
        if (primary != null) {
            alert.initOwner(primary);
            
            // Dimension Lock: Prevent shrinking on Linux while thread is blocked by modal dialog
            double oldMinW = primary.getMinWidth();
            double oldMinH = primary.getMinHeight();
            boolean wasMaximized = primary.isMaximized();
            
            if (wasMaximized) {
                primary.setMinWidth(primary.getWidth());
                primary.setMinHeight(primary.getHeight());
            }
            
            alert.showAndWait();
            
            // Restore original min dimensions
            primary.setMinWidth(oldMinW);
            primary.setMinHeight(oldMinH);
        } else {
            alert.showAndWait();
        }
    }

    public static java.util.Optional<String> showInputDialog(String title, String header, String content) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);

        Stage primary = App.getPrimaryStage();
        if (primary != null) {
            dialog.initOwner(primary);

            // Dimension Lock
            double oldMinW = primary.getMinWidth();
            double oldMinH = primary.getMinHeight();
            boolean wasMaximized = primary.isMaximized();

            if (wasMaximized) {
                primary.setMinWidth(primary.getWidth());
                primary.setMinHeight(primary.getHeight());
            }

            java.util.Optional<String> result = dialog.showAndWait();

            // Restore
            primary.setMinWidth(oldMinW);
            primary.setMinHeight(oldMinH);
            
            return result;
        }

        return dialog.showAndWait();
    }

    /**
     * Shows a user-friendly database error message.
     * Call this from catch blocks when database operations fail.
     * 
     * @param operation Description of what operation failed (e.g., "memuat data", "menyimpan data")
     * @param e The SQLException that was caught
     */
    public static void showDatabaseError(String operation, java.sql.SQLException e) {
        String message;
        
        // Translate common PostgreSQL error codes to user-friendly messages
        if (e.getSQLState() != null) {
            switch (e.getSQLState()) {
                case "23505": // Unique violation
                    message = "Data sudah ada di database (duplikat).";
                    break;
                case "23503": // Foreign key violation
                    message = "Data tidak dapat dihapus karena masih digunakan oleh data lain.";
                    break;
                case "08001": // Connection error
                case "08006":
                    message = "Tidak dapat terhubung ke database. Pastikan PostgreSQL berjalan.";
                    break;
                case "28P01": // Authentication failed
                    message = "Gagal login ke database. Periksa username dan password.";
                    break;
                default:
                    message = "Terjadi kesalahan: " + e.getMessage();
            }
        } else {
            message = "Kesalahan database: " + e.getMessage();
        }
        
        showAlert("Error " + operation, message, Alert.AlertType.ERROR);
        e.printStackTrace(); // Still log for debugging
    }
}
