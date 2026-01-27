package com.kecamatan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("login"), 400, 500);
        stage.setTitle("Login - Pendataan Kecamatan");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void setRoot(String fxml, double width, double height) throws IOException {
        scene.setRoot(loadFXML(fxml));
        Stage stage = (Stage) scene.getWindow();
        stage.setWidth(width);
        stage.setHeight(height);
        stage.centerOnScreen();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/kecamatan/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
