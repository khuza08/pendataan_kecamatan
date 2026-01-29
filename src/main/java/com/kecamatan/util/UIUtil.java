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
        
        // Set owner to prevent window shrinking on Linux
        Stage primary = App.getPrimaryStage();
        if (primary != null) {
            alert.initOwner(primary);
        }
        
        alert.showAndWait();
    }
}
