package com.kecamatan.controller;

import com.kecamatan.App;
import com.kecamatan.model.Desa;
import com.kecamatan.model.Warga;
import com.kecamatan.util.DatabaseUtil;
import com.kecamatan.util.ThreadManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.kecamatan.util.DataRefreshable;

public class WargaController implements Initializable, DataRefreshable {

    @Override
    public void refreshData() {
        loadDesaData();
        loadWargaData();
    }

    @FXML private TableView<Warga> wargaTable;
    @FXML private TableColumn<Warga, String> colNik;
    @FXML private TableColumn<Warga, String> colNama;
    @FXML private TableColumn<Warga, String> colJenkel;
    @FXML private TableColumn<Warga, LocalDate> colTglLahir;
    @FXML private TableColumn<Warga, String> colDesa;
    @FXML private TableColumn<Warga, String> colRW;
    @FXML private TableColumn<Warga, String> colRT;
    @FXML private TableColumn<Warga, String> colAlamat;

    @FXML private TextField nikField;
    @FXML private TextField namaField;
    @FXML private ComboBox<String> jenkelComboBox;
    @FXML private ComboBox<Desa> desaComboBox;
    @FXML private ComboBox<String> rwComboBox;
    @FXML private ComboBox<String> rtComboBox;
    @FXML private DatePicker tglLahirPicker;
    @FXML private TextArea alamatArea;

    private ObservableList<Warga> wargaList = FXCollections.observableArrayList();
    private ObservableList<Desa> desaList = FXCollections.observableArrayList();
    private int selectedId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNik.setCellValueFactory(cellData -> cellData.getValue().nikProperty());
        colNama.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        colJenkel.setCellValueFactory(cellData -> cellData.getValue().jenisKelaminProperty());
        colDesa.setCellValueFactory(cellData -> cellData.getValue().desaNamaProperty());
        colRW.setCellValueFactory(cellData -> cellData.getValue().rwProperty());
        colRT.setCellValueFactory(cellData -> cellData.getValue().rtProperty());
        colTglLahir.setCellValueFactory(cellData -> cellData.getValue().tanggalLahirProperty());
        colAlamat.setCellValueFactory(cellData -> cellData.getValue().alamatProperty());

        setupDesaComboBox();
        loadDesaData();
        loadWargaData();

        wargaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedId = newSel.getId();
                nikField.setText(newSel.getNik());
                namaField.setText(newSel.getNama());
                jenkelComboBox.getSelectionModel().select(newSel.getJenisKelamin());
                tglLahirPicker.setValue(newSel.getTanggalLahir());
                alamatArea.setText(newSel.getAlamat());
                
                for (Desa d : desaList) {
                    if (d.getId() == newSel.getDesaId()) {
                        desaComboBox.getSelectionModel().select(d);
                        // Trigger RW loading and then select the specific RW
                        loadRWData(d, newSel.getRw());
                        // Trigger RT loading and then select the specific RT
                        loadRTData(d, newSel.getRw(), newSel.getRt());
                        break;
                    }
                }
            }
        });

        // Cascading ComboBox listeners
        desaComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldDesa, newDesa) -> {
            if (newDesa != null) {
                loadRWData(newDesa, null);
            } else {
                rwComboBox.getItems().clear();
                rtComboBox.getItems().clear();
            }
        });

        rwComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldRw, newRw) -> {
            Desa currentDesa = desaComboBox.getSelectionModel().getSelectedItem();
            if (currentDesa != null && newRw != null) {
                loadRTData(currentDesa, newRw, null);
            } else {
                rtComboBox.getItems().clear();
            }
        });

        // Real-time NIK length & uniqueness validation
        nikField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                com.kecamatan.util.UIUtil.setErrorStyle(nikField, false);
                return;
            }

            // 1. Format check (must be 16 digits)
            boolean formatInvalid = newVal.length() != 16 || !newVal.matches("\\d+");
            if (formatInvalid) {
                com.kecamatan.util.UIUtil.setErrorStyle(nikField, true);
                return;
            }

            // 2. Uniqueness check (if format is valid)
            com.kecamatan.util.ThreadManager.execute(() -> {
                boolean isDuplicate = false;
                String sql = "SELECT COUNT(*) FROM warga WHERE nik = ? AND id != ?";
                try (Connection conn = DatabaseUtil.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, newVal);
                    pstmt.setInt(2, selectedId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        isDuplicate = rs.getInt(1) > 0;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                final boolean duplicateFound = isDuplicate;
                javafx.application.Platform.runLater(() -> {
                    // Only update if the field still contains the value we checked
                    if (nikField.getText().equals(newVal)) {
                        com.kecamatan.util.UIUtil.setErrorStyle(nikField, duplicateFound);
                    }
                });
            });
        });
    }

    private void setupDesaComboBox() {
        desaComboBox.setConverter(new StringConverter<Desa>() {
            @Override
            public String toString(Desa d) {
                return d == null ? "" : d.getNama();
            }
            @Override
            public Desa fromString(String string) { return null; }
        });
    }

    private void loadRWData(Desa d, String autoSelect) {
        com.kecamatan.util.ThreadManager.execute(() -> {
            ObservableList<String> rws = FXCollections.observableArrayList();
            String sql = "SELECT DISTINCT rw_number FROM desa_rtrw WHERE desa_id = ? ORDER BY rw_number";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, d.getId());
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    rws.add(rs.getString("rw_number"));
                }
                javafx.application.Platform.runLater(() -> {
                    rwComboBox.setItems(rws);
                    if (autoSelect != null) rwComboBox.getSelectionModel().select(autoSelect);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadRTData(Desa d, String rw, String autoSelect) {
        com.kecamatan.util.ThreadManager.execute(() -> {
            ObservableList<String> rts = FXCollections.observableArrayList();
            String sql = "SELECT rt_number FROM desa_rtrw WHERE desa_id = ? AND rw_number = ? ORDER BY rt_number";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, d.getId());
                pstmt.setString(2, rw);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    rts.add(rs.getString("rt_number"));
                }
                javafx.application.Platform.runLater(() -> {
                    rtComboBox.setItems(rts);
                    if (autoSelect != null) rtComboBox.getSelectionModel().select(autoSelect);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadDesaData() {
        com.kecamatan.util.ThreadManager.execute(() -> {
            ObservableList<Desa> tempDesa = FXCollections.observableArrayList();
            String sql = "SELECT d.*, k.nama as kecamatan_nama, (SELECT COUNT(*) FROM warga WHERE desa_id = d.id) as calculated_pop FROM desa d JOIN kecamatan k ON d.kecamatan_id = k.id ORDER BY d.nama";
            try (Connection conn = DatabaseUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    tempDesa.add(new Desa(
                        rs.getInt("id"),
                        rs.getInt("kecamatan_id"),
                        rs.getString("kecamatan_nama"),
                        rs.getString("nama"),
                        rs.getInt("calculated_pop"),
                        rs.getInt("jumlah_rt"),
                        rs.getInt("jumlah_rw")
                    ));
                }
                javafx.application.Platform.runLater(() -> {
                    desaList.setAll(tempDesa);
                    desaComboBox.setItems(desaList);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadWargaData() {
        com.kecamatan.util.ThreadManager.execute(() -> {
            ObservableList<Warga> tempWarga = FXCollections.observableArrayList();
            String sql = "SELECT w.*, d.nama as desa_nama FROM warga w JOIN desa d ON w.desa_id = d.id ORDER BY w.nama";
            try (Connection conn = DatabaseUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    tempWarga.add(new Warga(
                        rs.getInt("id"),
                        rs.getString("nik"),
                        rs.getString("nama"),
                        rs.getString("alamat"),
                        rs.getString("jenis_kelamin"),
                        rs.getInt("desa_id"),
                        rs.getString("desa_nama"),
                        rs.getString("rt"),
                        rs.getString("rw"),
                        rs.getDate("tanggal_lahir") != null ? rs.getDate("tanggal_lahir").toLocalDate() : null
                    ));
                }
                javafx.application.Platform.runLater(() -> {
                    wargaList.setAll(tempWarga);
                    wargaTable.setItems(wargaList);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleSave() {
        String nik = nikField.getText();
        String nama = namaField.getText();
        String jenkel = jenkelComboBox.getSelectionModel().getSelectedItem();
        Desa selectedDesa = desaComboBox.getSelectionModel().getSelectedItem();
        String selectedRw = rwComboBox.getSelectionModel().getSelectedItem();
        String selectedRt = rtComboBox.getSelectionModel().getSelectedItem();
        LocalDate tanggalLahir = tglLahirPicker.getValue();
        String alamat = alamatArea.getText();

        // Reset styles
        com.kecamatan.util.UIUtil.setErrorStyle(nikField, false);
        com.kecamatan.util.UIUtil.setErrorStyle(namaField, false);
        com.kecamatan.util.UIUtil.setErrorStyle(jenkelComboBox, false);
        com.kecamatan.util.UIUtil.setErrorStyle(desaComboBox, false);
        com.kecamatan.util.UIUtil.setErrorStyle(rwComboBox, false);
        com.kecamatan.util.UIUtil.setErrorStyle(rtComboBox, false);
        com.kecamatan.util.UIUtil.setErrorStyle(tglLahirPicker, false);

        boolean hasError = false;

        if (nik.isEmpty()) { com.kecamatan.util.UIUtil.setErrorStyle(nikField, true); hasError = true; }
        if (nama.isEmpty()) { com.kecamatan.util.UIUtil.setErrorStyle(namaField, true); hasError = true; }
        if (jenkel == null) { com.kecamatan.util.UIUtil.setErrorStyle(jenkelComboBox, true); hasError = true; }
        if (selectedDesa == null) { com.kecamatan.util.UIUtil.setErrorStyle(desaComboBox, true); hasError = true; }
        if (selectedRw == null) { com.kecamatan.util.UIUtil.setErrorStyle(rwComboBox, true); hasError = true; }
        if (selectedRt == null) { com.kecamatan.util.UIUtil.setErrorStyle(rtComboBox, true); hasError = true; }
        if (tanggalLahir == null) { com.kecamatan.util.UIUtil.setErrorStyle(tglLahirPicker, true); hasError = true; }

        if (hasError) return;

        // Add length and format validation
        if (nik.length() != 16 || !nik.matches("\\d+")) {
            com.kecamatan.util.UIUtil.setErrorStyle(nikField, true);
            return;
        }

        if (nama.length() > 100) {
            com.kecamatan.util.UIUtil.setErrorStyle(namaField, true);
            return;
        }

        String sql;
        if (selectedId == -1) {
            sql = "INSERT INTO warga (nik, nama, jenis_kelamin, desa_id, rw, rt, tanggal_lahir, alamat) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            sql = "UPDATE warga SET nik = ?, nama = ?, jenis_kelamin = ?, desa_id = ?, rw = ?, rt = ?, tanggal_lahir = ?, alamat = ? WHERE id = ?";
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nik);
            pstmt.setString(2, nama);
            pstmt.setString(3, jenkel);
            pstmt.setInt(4, selectedDesa.getId());
            pstmt.setString(5, selectedRw);
            pstmt.setString(6, selectedRt);
            pstmt.setDate(7, tanggalLahir != null ? java.sql.Date.valueOf(tanggalLahir) : null);
            pstmt.setString(8, alamat);
            if (selectedId != -1) pstmt.setInt(9, selectedId);
            
            pstmt.executeUpdate();

            loadWargaData();
            handleReset();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                com.kecamatan.util.UIUtil.showAlert("Error", "NIK sudah terdaftar!", Alert.AlertType.ERROR);
            } else {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedId == -1) return;
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Perform delete
            String sql = "DELETE FROM warga WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, selectedId);
                pstmt.executeUpdate();
            }

            loadWargaData();
            handleReset();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReset() {
        selectedId = -1;
        nikField.clear();
        namaField.clear();
        jenkelComboBox.getSelectionModel().clearSelection();
        desaComboBox.getSelectionModel().clearSelection();
        rwComboBox.getSelectionModel().clearSelection();
        rtComboBox.getSelectionModel().clearSelection();
        tglLahirPicker.setValue(null);
        alamatArea.clear();
        wargaTable.getSelectionModel().clearSelection();

        // Clear error styles
        com.kecamatan.util.UIUtil.setErrorStyle(nikField, false);
        com.kecamatan.util.UIUtil.setErrorStyle(namaField, false);
        com.kecamatan.util.UIUtil.setErrorStyle(jenkelComboBox, false);
        com.kecamatan.util.UIUtil.setErrorStyle(desaComboBox, false);
        com.kecamatan.util.UIUtil.setErrorStyle(rwComboBox, false);
        com.kecamatan.util.UIUtil.setErrorStyle(rtComboBox, false);
        com.kecamatan.util.UIUtil.setErrorStyle(tglLahirPicker, false);
    }

    @FXML private void goToDashboard() throws IOException { App.setRoot("dashboard", 1200, 800, true); }
    @FXML private void goToKecamatan() throws IOException { App.setRoot("kecamatan", 1200, 800, true); }
    @FXML private void goToDesa() throws IOException { App.setRoot("desa", 1200, 800, true); }
    @FXML private void goToLaporan() throws IOException { App.setRoot("laporan", 1200, 800, true); }
    @FXML private void handleLogout() throws IOException { App.setRoot("login", 800, 450); }
}
