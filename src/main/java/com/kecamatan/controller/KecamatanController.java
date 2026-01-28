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

public class KecamatanController implements Initializable {

    @FXML private TableView<Kecamatan> kecamatanTable;
    @FXML private TableColumn<Kecamatan, String> colKode;
    @FXML private TableColumn<Kecamatan, String> colNama;
    @FXML private TableColumn<Kecamatan, Integer> colDesa;
    @FXML private TableColumn<Kecamatan, Integer> colPopulasi;

    @FXML private TextField kodeField;
    @FXML private TextField namaField;
    @FXML private TextField desaField;
    @FXML private TextField populasiField;

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
                desaField.setText(String.valueOf(newSel.getJumlahDesa()));
                populasiField.setText(String.valueOf(newSel.getPopulasi()));
            }
        });
    }

    private void loadData() {
        kecamatanList.clear();
        String sql = "SELECT * FROM kecamatan ORDER BY kode";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                kecamatanList.add(new Kecamatan(
                    rs.getInt("id"),
                    rs.getString("kode"),
                    rs.getString("nama"),
                    rs.getInt("jumlah_desa"),
                    rs.getInt("populasi")
                ));
            }
            kecamatanTable.setItems(kecamatanList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        String kode = kodeField.getText();
        String nama = namaField.getText();
        String desaText = desaField.getText();
        String populasiText = populasiField.getText();

        if (kode.isEmpty() || nama.isEmpty() || desaText.isEmpty() || populasiText.isEmpty()) {
            showAlert("Validasi", "Semua field harus diisi!", Alert.AlertType.WARNING);
            return;
        }

        int desa, populasi;
        try {
            desa = Integer.parseInt(desaText);
            populasi = Integer.parseInt(populasiText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Jumlah Desa dan Populasi harus berupa angka!", Alert.AlertType.ERROR);
            return;
        }

        String sql;
        if (selectedId == -1) {
            sql = "INSERT INTO kecamatan (kode, nama, jumlah_desa, populasi) VALUES (?, ?, ?, ?)";
        } else {
            sql = "UPDATE kecamatan SET kode = ?, nama = ?, jumlah_desa = ?, populasi = ? WHERE id = ?";
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kode);
            pstmt.setString(2, nama);
            pstmt.setInt(3, desa);
            pstmt.setInt(4, populasi);
            if (selectedId != -1) pstmt.setInt(5, selectedId);
            
            pstmt.executeUpdate();
            loadData();
            handleReset();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
        desaField.clear();
        populasiField.clear();
        kecamatanTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void goToDashboard() throws IOException {
        App.setRoot("dashboard", 1200, 800, true);
    }

    @FXML
    private void goToDesa() throws IOException {
        App.setRoot("desa", 1200, 800, true);
    }

    @FXML
    private void handleLogout() throws IOException {
        App.setRoot("login", 400, 500);
    }
}
