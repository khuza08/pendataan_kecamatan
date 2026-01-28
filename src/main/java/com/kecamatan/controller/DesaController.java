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
    @FXML private TableColumn<Desa, Integer> colRT;
    @FXML private TableColumn<Desa, Integer> colRW;

    @FXML private ComboBox<Kecamatan> kecamatanComboBox;
    @FXML private TextField namaField;
    @FXML private TextField populasiField;
    @FXML private TextField rtField;
    @FXML private TextField rwField;

    private ObservableList<Desa> desaList = FXCollections.observableArrayList();
    private ObservableList<Kecamatan> kecamatanList = FXCollections.observableArrayList();
    private int selectedId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colKecamatan.setCellValueFactory(cellData -> cellData.getValue().kecamatanNamaProperty());
        colNama.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        colPopulasi.setCellValueFactory(cellData -> cellData.getValue().populasiProperty().asObject());
        colRT.setCellValueFactory(cellData -> cellData.getValue().jumlahRtProperty().asObject());
        colRW.setCellValueFactory(cellData -> cellData.getValue().jumlahRwProperty().asObject());

        setupComboBox();
        loadKecamatan();
        loadDesa();

        desaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedId = newSel.getId();
                namaField.setText(newSel.getNama());
                populasiField.setText(String.valueOf(newSel.getPopulasi()));
                rtField.setText(String.valueOf(newSel.getJumlahRt()));
                rwField.setText(String.valueOf(newSel.getJumlahRw()));
                
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
        com.kecamatan.util.ThreadManager.execute(() -> {
            ObservableList<Kecamatan> tempKec = FXCollections.observableArrayList();
            String sql = "SELECT * FROM kecamatan ORDER BY nama";
            try (Connection conn = DatabaseUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    tempKec.add(new Kecamatan(
                        rs.getInt("id"),
                        rs.getString("kode"),
                        rs.getString("nama"),
                        rs.getInt("jumlah_desa"),
                        rs.getInt("populasi")
                    ));
                }
                javafx.application.Platform.runLater(() -> {
                    kecamatanList.setAll(tempKec);
                    kecamatanComboBox.setItems(kecamatanList);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadDesa() {
        com.kecamatan.util.ThreadManager.execute(() -> {
            ObservableList<Desa> tempDesa = FXCollections.observableArrayList();
            String sql = "SELECT d.*, k.nama as kecamatan_nama FROM desa d JOIN kecamatan k ON d.kecamatan_id = k.id ORDER BY d.nama";
            try (Connection conn = DatabaseUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    tempDesa.add(new Desa(
                        rs.getInt("id"),
                        rs.getInt("kecamatan_id"),
                        rs.getString("kecamatan_nama"),
                        rs.getString("nama"),
                        rs.getInt("populasi"),
                        rs.getInt("jumlah_rt"),
                        rs.getInt("jumlah_rw")
                    ));
                }
                javafx.application.Platform.runLater(() -> {
                    desaList.setAll(tempDesa);
                    desaTable.setItems(desaList);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleSave() {
        Kecamatan selectedKec = kecamatanComboBox.getSelectionModel().getSelectedItem();
        String nama = namaField.getText();
        String populasiText = populasiField.getText();
        String rtText = rtField.getText();
        String rwText = rwField.getText();

        if (selectedKec == null || nama.isEmpty() || populasiText.isEmpty() || rtText.isEmpty() || rwText.isEmpty()) {
            showAlert("Validasi", "Semua field harus diisi!", Alert.AlertType.WARNING);
            return;
        }

        int populasi, rt, rw;
        try {
            populasi = Integer.parseInt(populasiText);
            rt = Integer.parseInt(rtText);
            rw = Integer.parseInt(rwText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Populasi, RT, dan RW harus berupa angka!", Alert.AlertType.ERROR);
            return;
        }

        String sql;
        if (selectedId == -1) {
            sql = "INSERT INTO desa (kecamatan_id, nama, populasi, jumlah_rt, jumlah_rw) VALUES (?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE desa SET kecamatan_id = ?, nama = ?, populasi = ?, jumlah_rt = ?, jumlah_rw = ? WHERE id = ?";
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, selectedKec.getId());
            pstmt.setString(2, nama);
            pstmt.setInt(3, populasi);
            pstmt.setInt(4, rt);
            pstmt.setInt(5, rw);
            if (selectedId != -1) pstmt.setInt(6, selectedId);
            
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

    @FXML private void goToDashboard() throws IOException { App.setRoot("dashboard", 1200, 800, true); }
    @FXML private void goToKecamatan() throws IOException { App.setRoot("kecamatan", 1200, 800, true); }
    @FXML private void goToWarga() throws IOException { App.setRoot("warga", 1200, 800, true); }
    @FXML private void goToLaporan() throws IOException { App.setRoot("laporan", 1200, 800, true); }
    @FXML private void handleLogout() throws IOException { App.setRoot("login", 400, 500); }
}
