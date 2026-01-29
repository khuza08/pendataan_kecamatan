package com.kecamatan.controller;

import com.kecamatan.App;
import javafx.fxml.FXML;
import java.io.IOException;

import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import java.net.URL;
import java.util.ResourceBundle;

import com.kecamatan.util.DatabaseUtil;
import com.kecamatan.util.ThreadManager;
import com.kecamatan.util.DataRefreshable;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import java.sql.*;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable, DataRefreshable {

    @FXML private LineChart<String, Number> wargaChart;
    @FXML private Text totalWargaText;
    @FXML private Text lakiText;
    @FXML private Text perempuanText;
    @FXML private Text rtrwText;
    @FXML private Text totalKecamatanText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        refreshData();
    }

    @Override
    public void refreshData() {
        ThreadManager.execute(() -> {
            try (Connection conn = DatabaseUtil.getConnection()) {
                // 1. Total Warga
                int totalWarga = 0;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM warga")) {
                    if (rs.next()) totalWarga = rs.getInt(1);
                }


                // 2. L / P Count
                int male = 0, female = 0;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT jenis_kelamin, COUNT(*) FROM warga GROUP BY jenis_kelamin")) {
                    while (rs.next()) {
                        String jk = rs.getString(1);
                        if (jk != null) {
                            if (jk.equalsIgnoreCase("L") || jk.equalsIgnoreCase("Laki-laki")) male += rs.getInt(2);
                            else if (jk.equalsIgnoreCase("P") || jk.equalsIgnoreCase("Perempuan")) female += rs.getInt(2);
                        }
                    }
                }

                // 3. RT / RW Count
                int totalRT = 0, totalRW = 0;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT SUM(jumlah_rt), SUM(jumlah_rw) FROM desa")) {
                    if (rs.next()) {
                        totalRT = rs.getInt(1);
                        totalRW = rs.getInt(2);
                    }
                }

                // 4. Total Kecamatan
                int totalKec = 0;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM kecamatan")) {
                    if (rs.next()) totalKec = rs.getInt(1);
                }

                // 5. Chart Data (Top 6 Kecamatan)
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Populasi per Kecamatan");
                String chartSql = "SELECT k.nama, (SELECT COUNT(*) FROM warga w JOIN desa d ON w.desa_id = d.id WHERE d.kecamatan_id = k.id) as calculated_pop " +
                                 "FROM kecamatan k ORDER BY calculated_pop DESC LIMIT 6";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(chartSql)) {
                    while (rs.next()) {
                        series.getData().add(new XYChart.Data<>(rs.getString("nama"), rs.getInt("calculated_pop")));
                    }
                }

                // UI Updates
                final int fw = totalWarga, fm = male, ff = female, frt = totalRT, frw = totalRW, fk = totalKec;
                Platform.runLater(() -> {
                    totalWargaText.setText(String.format("%,d", fw));
                    lakiText.setText(String.format("%,d", fm));
                    perempuanText.setText(String.format("%,d", ff));
                    rtrwText.setText(String.format("%d / %d", frt, frw));
                    totalKecamatanText.setText(String.valueOf(fk));
                    
                    wargaChart.getData().clear();
                    wargaChart.getData().add(series);

                    // Apply dynamic colors to bars/nodes
                    String[] colors = {"#3498db", "#2ecc71", "#9b59b6", "#e67e22", "#e74c3c", "#1abc9c", "#34495e", "#f1c40f"};
                    for (int i = 0; i < series.getData().size(); i++) {
                        final int index = i;
                        XYChart.Data<String, Number> data = series.getData().get(i);
                        Platform.runLater(() -> {
                            if (data.getNode() != null) {
                                String color = colors[index % colors.length];
                                data.getNode().setStyle("-fx-bar-fill: " + color + "; -fx-background-color: " + color + ";");
                            }
                        });
                    }
                });

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void goToKecamatan() throws IOException {
        App.setRoot("kecamatan", 1200, 800, true);
    }

    @FXML
    private void goToDesa() throws IOException {
        App.setRoot("desa", 1200, 800, true);
    }

    @FXML
    private void goToWarga() throws IOException {
        App.setRoot("warga", 1200, 800, true);
    }

    @FXML
    private void goToLaporan() throws IOException {
        App.setRoot("laporan", 1200, 800, true);
    }

    @FXML
    private void handleLogout() throws IOException {
        App.setRoot("login", 400, 500);
    }
}
