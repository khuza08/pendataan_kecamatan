package com.kecamatan.util;

import com.kecamatan.App;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class UIUtil {

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
        
        // Safety measure: re-maximize if it was maximized before
        if (primary != null && primary.isMaximized()) {
            primary.setMaximized(false);
            primary.setMaximized(true);
        }
    }
}
