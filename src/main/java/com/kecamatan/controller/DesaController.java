package com.kecamatan.controller;

import com.kecamatan.App;
import com.kecamatan.model.Desa;
import com.kecamatan.model.Kecamatan;
import com.kecamatan.util.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class DesaController implements Initializable {

    @FXML private TableView<Desa> desaTable;
    @FXML private TableColumn<Desa, String> colKecamatan;
    @FXML private TableColumn<Desa, String> colNama;
    @FXML private TableColumn<Desa, Integer> colPopulasi;

    @FXML private ComboBox<Kecamatan> kecamatanComboBox;
    @FXML private TextField namaField;
    @FXML private TextField populasiField;

    private ObservableList<Desa> desaList = FXCollections.observableArrayList();
    private ObservableList<Kecamatan> kecamatanList = FXCollections.observableArrayList();
    private int selectedId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colKecamatan.setCellValueFactory(cellData -> cellData.getValue().kecamatanNamaProperty());
        colNama.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        colPopulasi.setCellValueFactory(cellData -> cellData.getValue().populasiProperty().asObject());

        setupComboBox();
        loadKecamatan();
        loadDesa();

        desaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedId = newSel.getId();
                namaField.setText(newSel.getNama());
                populasiField.setText(String.valueOf(newSel.getPopulasi()));
                
                for (Kecamatan k : kecamatanList) {
                    if (k.getId() == newSel.getKecamatanId()) {
                        kecamatanComboBox.setSelectionModel(null); // Force update
                        kecamatanComboBox.getSelectionModel().select(k);
                        break;
                    }
                }
            }
        });
    }

    private void setupComboBox() {
        kecamatanComboBox.setConverter(new StringConverter<Kecamatan>() {
            @Override
            public String toString(Kecamatan k) {
                return k == null ? "" : k.getNama();
            }

            @Override
            public Kecamatan fromString(String string) {
                return null;
            }
        });
    }

    private void loadKecamatan() {
        kecamatanList.clear();
        String sql = "SELECT * FROM kecamatan ORDER BY nama";
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
            kecamatanComboBox.setItems(kecamatanList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDesa() {
        desaList.clear();
        String sql = "SELECT d.*, k.nama as kecamatan_nama FROM desa d JOIN kecamatan k ON d.kecamatan_id = k.id ORDER BY d.nama";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                desaList.add(new Desa(
                    rs.getInt("id"),
                    rs.getInt("kecamatan_id"),
                    rs.getString("kecamatan_nama"),
                    rs.getString("nama"),
                    rs.getInt("populasi")
                ));
            }
            desaTable.setItems(desaList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        Kecamatan selectedKec = kecamatanComboBox.getSelectionModel().getSelectedItem();
        String nama = namaField.getText();
        String populasiText = populasiField.getText();

        if (selectedKec == null || nama.isEmpty() || populasiText.isEmpty()) {
            showAlert("Validasi", "Semua field harus diisi!", Alert.AlertType.WARNING);
            return;
        }

        int populasi;
        try {
            populasi = Integer.parseInt(populasiText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Populasi harus berupa angka!", Alert.AlertType.ERROR);
            return;
        }

        String sql;
        if (selectedId == -1) {
            sql = "INSERT INTO desa (kecamatan_id, nama, populasi) VALUES (?, ?, ?)";
        } else {
            sql = "UPDATE desa SET kecamatan_id = ?, nama = ?, populasi = ? WHERE id = ?";
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, selectedKec.getId());
            pstmt.setString(2, nama);
            pstmt.setInt(3, populasi);
            if (selectedId != -1) pstmt.setInt(4, selectedId);
            
            pstmt.executeUpdate();
            loadDesa();
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
        String sql = "DELETE FROM desa WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, selectedId);
            pstmt.executeUpdate();
            loadDesa();
            handleReset();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReset() {
        selectedId = -1;
        namaField.clear();
        populasiField.clear();
        rtField.clear();
        rwField.clear();
        kecamatanComboBox.getSelectionModel().clearSelection();
        desaTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void goToDashboard() throws IOException {
        App.setRoot("dashboard", 1200, 800, true);
    }

    @FXML
    private void goToKecamatan() throws IOException {
        App.setRoot("kecamatan", 1200, 800, true);
    }

    @FXML
    private void handleLogout() throws IOException {
        App.setRoot("login", 400, 500);
    }
}
