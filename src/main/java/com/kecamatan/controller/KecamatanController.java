package com.kecamatan.controller;

import com.kecamatan.App;
import com.kecamatan.model.Kecamatan;
import com.kecamatan.util.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import com.kecamatan.util.DataRefreshable;

public class KecamatanController implements Initializable, DataRefreshable {
    
    @Override
    public void refreshData() {
        loadData();
    }

    @FXML private TableView<Kecamatan> kecamatanTable;
    @FXML private TableColumn<Kecamatan, String> colKode;
    @FXML private TableColumn<Kecamatan, String> colNama;
    @FXML private TableColumn<Kecamatan, Integer> colDesa;
    @FXML private TableColumn<Kecamatan, Integer> colPopulasi;

    @FXML private TextField kodeField;
    @FXML private TextField namaField;

    private ObservableList<Kecamatan> kecamatanList = FXCollections.observableArrayList();
    private int selectedId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colKode.setCellValueFactory(cellData -> cellData.getValue().kodeProperty());
        colNama.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        colDesa.setCellValueFactory(cellData -> cellData.getValue().jumlahDesaProperty().asObject());
        colPopulasi.setCellValueFactory(cellData -> cellData.getValue().populasiProperty().asObject());

        loadData();

        kecamatanTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedId = newSel.getId();
                kodeField.setText(newSel.getKode());
                namaField.setText(newSel.getNama());
            }
        });
    }

    private void loadData() {
        com.kecamatan.util.ThreadManager.execute(() -> {
            ObservableList<Kecamatan> tempData = FXCollections.observableArrayList();
            String sql = "SELECT k.*, " +
                         "(SELECT COUNT(*) FROM desa WHERE kecamatan_id = k.id) as calculated_desa_count, " +
                         "(SELECT COUNT(*) FROM warga w JOIN desa d ON w.desa_id = d.id WHERE d.kecamatan_id = k.id) as calculated_pop " +
                         "FROM kecamatan k ORDER BY k.kode";
            try (Connection conn = DatabaseUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    tempData.add(new Kecamatan(
                        rs.getInt("id"),
                        rs.getString("kode"),
                        rs.getString("nama"),
                        rs.getInt("calculated_desa_count"),
                        rs.getInt("calculated_pop")
                    ));
                }
                
                javafx.application.Platform.runLater(() -> {
                    kecamatanList.setAll(tempData);
                    kecamatanTable.setItems(kecamatanList);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleSave() {
        String kode = kodeField.getText();
        String nama = namaField.getText();

        // Reset styles
        com.kecamatan.util.UIUtil.setErrorStyle(kodeField, false);
        com.kecamatan.util.UIUtil.setErrorStyle(namaField, false);

        boolean hasError = false;

        if (kode.isEmpty()) { com.kecamatan.util.UIUtil.setErrorStyle(kodeField, true); hasError = true; }
        if (nama.isEmpty()) { com.kecamatan.util.UIUtil.setErrorStyle(namaField, true); hasError = true; }

        if (hasError) return;

        if (kode.length() > 10) {
            com.kecamatan.util.UIUtil.setErrorStyle(kodeField, true);
            return;
        }

        if (nama.length() > 100) {
            com.kecamatan.util.UIUtil.setErrorStyle(namaField, true);
            return;
        }

        String sql;
        if (selectedId == -1) {
            sql = "INSERT INTO kecamatan (kode, nama) VALUES (?, ?)";
        } else {
            sql = "UPDATE kecamatan SET kode = ?, nama = ? WHERE id = ?";
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kode);
            pstmt.setString(2, nama);
            if (selectedId != -1) pstmt.setInt(3, selectedId);
            
            pstmt.executeUpdate();
            loadData();
            handleReset();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedId == -1) return;
        String sql = "DELETE FROM kecamatan WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, selectedId);
            pstmt.executeUpdate();
            loadData();
            handleReset();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReset() {
        selectedId = -1;
        kodeField.clear();
        namaField.clear();
        kecamatanTable.getSelectionModel().clearSelection();

        // Clear error styles
        com.kecamatan.util.UIUtil.setErrorStyle(kodeField, false);
        com.kecamatan.util.UIUtil.setErrorStyle(namaField, false);
    }

    @FXML
    private void goToDashboard() throws IOException {
        App.setRoot("dashboard", 1200, 800, true);
    }

    @FXML private void goToDesa() throws IOException { App.setRoot("desa", 1200, 800, true); }
    @FXML private void goToWarga() throws IOException { App.setRoot("warga", 1200, 800, true); }
    @FXML private void goToLaporan() throws IOException { App.setRoot("laporan", 1200, 800, true); }
    @FXML private void handleLogout() throws IOException { App.setRoot("login", 400, 500); }
}
