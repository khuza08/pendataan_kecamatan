package com.kecamatan.controller;

import com.kecamatan.App;
import com.kecamatan.model.Warga;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import java.io.IOException;

import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import java.net.URL;
import java.util.ResourceBundle;

import com.kecamatan.util.DatabaseUtil;
import com.kecamatan.util.ThreadManager;
import com.kecamatan.util.DataRefreshable;
import com.kecamatan.util.RBACUtil;
import com.kecamatan.util.UIUtil;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    @FXML private Text totalDesaText;
    @FXML private Button btnKecamatan;
    @FXML private Button btnDesa;
    @FXML private Button btnWarga;
    @FXML private Button btnDashboard;
    @FXML private Button btnLaporan;
    @FXML private Button btnProfil;
    @FXML private VBox recentActivitiesBox;
    @FXML private VBox adminContent;
    @FXML private VBox wargaContent;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
 
    @FXML private TableView<Warga> dashboardWargaTable;
    @FXML private TableColumn<Warga, String> colNik;
    @FXML private TableColumn<Warga, String> colNama;
    @FXML private TableColumn<Warga, String> colDesa;
    @FXML private TableColumn<Warga, String> colKecamatan;
    @FXML private TableColumn<Warga, String> colJenkel;
 
    private ObservableList<Warga> dashboardWargaList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        applyRBAC();
        
        // Initialize table columns
        if (colNik != null) {
            colNik.setCellValueFactory(cellData -> cellData.getValue().nikProperty());
            colNama.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
            colDesa.setCellValueFactory(cellData -> cellData.getValue().desaNamaProperty());
            colKecamatan.setCellValueFactory(cellData -> cellData.getValue().kecamatanNamaProperty());
            colJenkel.setCellValueFactory(cellData -> cellData.getValue().jenisKelaminProperty());
        }
        
        refreshData();
    }

    private void applyRBAC() {
        // Use centralized RBAC utility with full support for both admin and warga buttons
        RBACUtil.applyFullRBAC(userNameLabel, userRoleLabel, btnProfil,
            btnKecamatan, btnDesa, btnWarga, btnLaporan, btnDashboard);

        if (!com.kecamatan.util.UserSession.isAdmin()) {
            // Hide Admin Dashboard Content, Show Warga Welcome
            if (adminContent != null) { adminContent.setManaged(false); adminContent.setVisible(false); }
            if (wargaContent != null) { wargaContent.setManaged(true); wargaContent.setVisible(true); }
        } else {
            // Admin View
            if (adminContent != null) { adminContent.setManaged(true); adminContent.setVisible(true); }
            if (wargaContent != null) { wargaContent.setManaged(false); wargaContent.setVisible(false); }
        }
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

                // 5. Total Desa
                int totalDesa = 0;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM desa")) {
                    if (rs.next()) totalDesa = rs.getInt(1);
                }

                // 6. Chart Data (Top 6 Kecamatan)
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
 
                // 7. Recent Warga Table
                ObservableList<Warga> recentWarga = FXCollections.observableArrayList();
                String recentSql = "SELECT w.*, d.nama as desa_nama, k.nama as kecamatan_nama " +
                                  "FROM warga w " +
                                  "JOIN desa d ON w.desa_id = d.id " +
                                  "JOIN kecamatan k ON d.kecamatan_id = k.id " +
                                  "ORDER BY w.id DESC LIMIT 10";
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(recentSql)) {
                    while (rs.next()) {
                        recentWarga.add(new Warga(
                            rs.getInt("id"),
                            rs.getString("nik"),
                            rs.getString("nama"),
                            rs.getString("alamat"),
                            rs.getString("jenis_kelamin"),
                            rs.getString("agama"),
                            rs.getString("status_kawin"),
                            rs.getString("pekerjaan"),
                            rs.getString("no_hp"),
                            rs.getInt("desa_id"),
                            rs.getString("desa_nama"),
                            rs.getString("kecamatan_nama"),
                            rs.getString("rt"),
                            rs.getString("rw"),
                            rs.getString("tanggal_lahir") != null ? java.time.LocalDate.parse(rs.getString("tanggal_lahir"), java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")) : null
                        ));
                    }
                }

                // UI Updates
                final int fw = totalWarga, fm = male, ff = female, frt = totalRT, frw = totalRW, fk = totalKec, fd = totalDesa;
                Platform.runLater(() -> {
                    totalWargaText.setText(String.format("%,d", fw));
                    lakiText.setText(String.format("%,d", fm));
                    perempuanText.setText(String.format("%,d", ff));
                    rtrwText.setText(String.format("%d / %d", frt, frw));
                    totalKecamatanText.setText(String.valueOf(fk));
                    totalDesaText.setText(String.valueOf(fd));
                    
                    if (dashboardWargaTable != null) {
                        dashboardWargaList.setAll(recentWarga);
                        dashboardWargaTable.setItems(dashboardWargaList);
                    }
                    
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
                UIUtil.showDatabaseError("Memuat Dashboard", e);
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
    private void goToProfil() throws IOException {
        App.setRoot("profil", 1200, 800, true);
    }

    @FXML
    private void handleLogout() throws IOException {
        com.kecamatan.util.UserSession.logout();
        App.setRoot("login", 800, 450);
    }
}
