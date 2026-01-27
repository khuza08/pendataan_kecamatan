package com.kecamatan.controller;

import com.kecamatan.App;
import javafx.fxml.FXML;
import java.io.IOException;

import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private LineChart<String, Number> wargaChart;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupChart();
    }

    private void setupChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Pertumbuhan 2024");
        
        series.getData().add(new XYChart.Data<>("Jan", 1200000));
        series.getData().add(new XYChart.Data<>("Feb", 1215000));
        series.getData().add(new XYChart.Data<>("Mar", 1228000));
        series.getData().add(new XYChart.Data<>("Apr", 1235000));
        series.getData().add(new XYChart.Data<>("Mei", 1242000));
        series.getData().add(new XYChart.Data<>("Jun", 1245678));
        
        wargaChart.getData().add(series);
    }

    @FXML
    private void handleLogout() throws IOException {
        App.setRoot("login", 400, 500);
    }
}
