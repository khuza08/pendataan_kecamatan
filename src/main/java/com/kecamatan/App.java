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

public class App extends Application {

    private static Scene scene;
    private static String currentFxml = "login";
    private static double currentWidth = 400;
    private static double currentHeight = 500;

    @Override
    public void start(Stage stage) throws IOException {
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

    public static void refresh() throws IOException {
        scene.setRoot(loadFXML(currentFxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        String path = "/com/kecamatan/view/" + fxml + ".fxml";
        
        // Try to load from src/main/resources first for "Live" experience
        File devFile = new File("src/main/resources" + path);
        URL resourceUrl;
        
        if (devFile.exists()) {
            resourceUrl = devFile.toURI().toURL();
        } else {
            resourceUrl = App.class.getResource(path);
        }
        
        if (resourceUrl == null) {
            throw new IOException("Cannot find FXML file: " + path);
        }
        
        FXMLLoader fxmlLoader = new FXMLLoader(resourceUrl);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
