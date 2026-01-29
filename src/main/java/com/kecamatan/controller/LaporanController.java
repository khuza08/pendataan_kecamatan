package com.kecamatan.controller;

import com.kecamatan.App;
import com.kecamatan.model.Desa;
import com.kecamatan.util.DatabaseUtil;
import com.kecamatan.util.ThreadManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import com.kecamatan.util.DataRefreshable;

public class LaporanController implements Initializable, DataRefreshable {

    @Override
    public void refreshData() {
        loadData();
    }

    @FXML private Text totalKecamatanText;
    @FXML private Text totalDesaText;
    @FXML private Text totalWargaText;
    @FXML private PieChart genderChart;
    @FXML private BarChart<String, Number> populationChart;
    @FXML private TableView<Desa> summaryTable;
    @FXML private TableColumn<Desa, String> colDesa;
    @FXML private TableColumn<Desa, String> colKec;
    @FXML private TableColumn<Desa, Integer> colPop;
    @FXML private TableColumn<Desa, Integer> colRT;
    @FXML private TableColumn<Desa, Integer> colRW;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colDesa.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        colKec.setCellValueFactory(cellData -> cellData.getValue().kecamatanNamaProperty());
        colPop.setCellValueFactory(cellData -> cellData.getValue().populasiProperty().asObject());
        colRT.setCellValueFactory(cellData -> cellData.getValue().jumlahRtProperty().asObject());
        colRW.setCellValueFactory(cellData -> cellData.getValue().jumlahRwProperty().asObject());

        loadData();
    }

    private void loadData() {
        ThreadManager.execute(() -> {
            try (Connection conn = DatabaseUtil.getConnection()) {
                // 1. Load Summary Counts
                int totalKec = 0;
                int totalDesa = 0;
                int totalWarga = 0;

                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SELECT (SELECT COUNT(*) FROM kecamatan) as kec, (SELECT COUNT(*) FROM desa) as desa, (SELECT COUNT(*) FROM warga) as warga");
                    if (rs.next()) {
                        totalKec = rs.getInt("kec");
                        totalDesa = rs.getInt("desa");
                        totalWarga = rs.getInt("warga");
                    }
                }

                // 2. Load Gender Data
                ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SELECT jenis_kelamin, COUNT(*) as count FROM warga GROUP BY jenis_kelamin");
                    while (rs.next()) {
                        String jk = rs.getString("jenis_kelamin");
                        if (jk == null) continue;
                        
                        String label;
                        if (jk.equalsIgnoreCase("L") || jk.equalsIgnoreCase("Laki-laki")) {
                            label = "Laki-laki";
                        } else if (jk.equalsIgnoreCase("P") || jk.equalsIgnoreCase("Perempuan")) {
                            label = "Perempuan";
                        } else {
                            label = jk; // Fallback to original value
                        }
                        
                        pieData.add(new PieChart.Data(label, rs.getInt("count")));
                    }
                }

                // 3. Load Population by Kecamatan (Bar Chart)
                XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
                barSeries.setName("Populasi");
                try (Statement stmt = conn.createStatement()) {
                    String barSql = "SELECT k.nama, (SELECT COUNT(*) FROM warga w JOIN desa d ON w.desa_id = d.id WHERE d.kecamatan_id = k.id) as total_pop " +
                                   "FROM kecamatan k ORDER BY total_pop DESC LIMIT 10";
                    ResultSet rs = stmt.executeQuery(barSql);
                    while (rs.next()) {
                        barSeries.getData().add(new XYChart.Data<>(rs.getString("nama"), rs.getInt("total_pop")));
                    }
                }

                // 4. Load Summary Table Data
                ObservableList<Desa> desaList = FXCollections.observableArrayList();
                try (Statement stmt = conn.createStatement()) {
                    String summarySql = "SELECT d.id, d.kecamatan_id, d.nama, d.jumlah_rt, d.jumlah_rw, k.nama as kecamatan_nama, " +
                                       "(SELECT COUNT(*) FROM warga WHERE desa_id = d.id) as calculated_pop " +
                                       "FROM desa d JOIN kecamatan k ON d.kecamatan_id = k.id ORDER BY calculated_pop DESC";
                    ResultSet rs = stmt.executeQuery(summarySql);
                    while (rs.next()) {
                        desaList.add(new Desa(
                            rs.getInt("id"),
                            rs.getInt("kecamatan_id"),
                            rs.getString("kecamatan_nama"),
                            rs.getString("nama"),
                            rs.getInt("calculated_pop"),
                            rs.getInt("jumlah_rt"),
                            rs.getInt("jumlah_rw")
                        ));
                    }
                }

                // Update UI on FX Thread
                int finalTotalKec = totalKec;
                int finalTotalDesa = totalDesa;
                int finalTotalWarga = totalWarga;
                Platform.runLater(() -> {
                    totalKecamatanText.setText(String.valueOf(finalTotalKec));
                    totalDesaText.setText(String.valueOf(finalTotalDesa));
                    totalWargaText.setText(String.valueOf(finalTotalWarga));
                    genderChart.setData(pieData);
                    populationChart.getData().clear();
                    populationChart.getData().add(barSeries);
                    summaryTable.setItems(desaList);
                });

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML private void goToDashboard() throws IOException { App.setRoot("dashboard", 1200, 800, true); }
    @FXML private void goToKecamatan() throws IOException { App.setRoot("kecamatan", 1200, 800, true); }
    @FXML private void goToDesa() throws IOException { App.setRoot("desa", 1200, 800, true); }
    @FXML private void goToWarga() throws IOException { App.setRoot("warga", 1200, 800, true); }
    @FXML private void handleLogout() throws IOException { App.setRoot("login", 400, 500); }
}
