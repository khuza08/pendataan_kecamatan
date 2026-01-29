package com.kecamatan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.scene.input.KeyCode;
import java.io.File;
import java.net.URL;
import javafx.application.Platform;
import com.kecamatan.util.DataRefreshable;
import java.util.Map;
import java.util.HashMap;

public class App extends Application {

    private static Scene scene;
    private static Stage primaryStage;
    private static String currentFxml = "login";
    private static double currentWidth = 400;
    private static double currentHeight = 500;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        scene = new Scene(loadFXML(currentFxml), currentWidth, currentHeight);
        
        // F5 Refresh Shortcut
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F5) {
                try {
                    System.out.println("Refreshing UI...");
                    refresh();
                } catch (IOException e) {
                    System.err.println("Failed to refresh: " + e.getMessage());
                }
            }
        });

        stage.setTitle("Pendataan Kecamatan");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setRoot(String fxml, double width, double height, boolean maximized) throws IOException {
        currentFxml = fxml;
        currentWidth = width;
        currentHeight = height;

        // 1. Swap the content first
        refresh();

        // 2. Adjust the window state
        Stage stage = (Stage) scene.getWindow();
        if (stage != null) {
            stage.setResizable(true); // Always allow resize for dashboard
            
            Platform.runLater(() -> {
                if (maximized) {
                    stage.setMaximized(true);
                } else {
                    stage.setMaximized(false);
                    stage.setWidth(width);
                    stage.setHeight(height);
                    stage.centerOnScreen();
                }
            });
        }
    }

    public static void setRoot(String fxml, double width, double height) throws IOException {
        setRoot(fxml, width, height, false);
        Stage stage = (Stage) scene.getWindow();
        if (stage != null) {
            stage.setResizable(false); // Locking login window
        }
    }

    private static final Map<String, Parent> viewCache = new HashMap<>();
    private static final Map<String, Object> controllerCache = new HashMap<>();

    public static void preloadViews(String... fxmls) {
        com.kecamatan.util.ThreadManager.execute(() -> {
            for (String fxml : fxmls) {
                try {
                    // This calls loadFXML which populates the cache
                    Platform.runLater(() -> {
                        try {
                            loadFXML(fxml);
                            System.out.println("Preloaded view: " + fxml);
                        } catch (IOException e) {
                            System.err.println("Failed to preload " + fxml + ": " + e.getMessage());
                        }
                    });
                    // Small delay between preloads to avoid CPU spikes during navigation
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public static void refresh() throws IOException {
        Parent root = loadFXML(currentFxml);
        scene.setRoot(root);
        
        Object controller = controllerCache.get(currentFxml);
        if (controller instanceof DataRefreshable) {
            ((DataRefreshable) controller).refreshData();
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        if (viewCache.containsKey(fxml)) {
            return viewCache.get(fxml);
        }

        String path = "/com/kecamatan/view/" + fxml + ".fxml";
        URL resourceUrl = App.class.getResource(path);
        
        if (resourceUrl == null) {
            // Fallback to dev path if resource is not found (for dev environment)
            File devFile = new File("src/main/resources" + path);
            if (devFile.exists()) {
                resourceUrl = devFile.toURI().toURL();
            }
        }
        
        if (resourceUrl == null) {
            throw new IOException("Cannot find FXML file: " + path);
        }
        
        FXMLLoader fxmlLoader = new FXMLLoader(resourceUrl);
        Parent root = fxmlLoader.load();
        Object controller = fxmlLoader.getController();
        
        // Cache all views except login (to allow fresh state if needed)
        if (!fxml.equals("login")) {
            viewCache.put(fxml, root);
            if (controller != null) {
                controllerCache.put(fxml, controller);
            }
        }
        
        return root;
    }

    public static void main(String[] args) {
        launch();
    }
}
