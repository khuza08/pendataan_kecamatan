package com.kecamatan.controller;

import com.kecamatan.App;
import com.kecamatan.util.DatabaseUtil;
import com.kecamatan.util.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ProfilController implements Initializable {

    @FXML private TextField nikField;
    @FXML private TextField namaField;
    @FXML private TextField jenkelField;
    @FXML private TextField tglLahirField;
    @FXML private TextField desaField;
    @FXML private TextField rtrwField;
    @FXML private TextField roleField;
    @FXML private TextArea alamatArea;

    @FXML private Button btnKecamatan;
    @FXML private Button btnDesa;
    @FXML private Button btnWarga;
    @FXML private Button btnLaporan;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        applyRBAC();
        loadProfileData();
    }

    private void applyRBAC() {
        if (userNameLabel != null) userNameLabel.setText(UserSession.getUsername());
        if (userRoleLabel != null) {
            String role = UserSession.getRole();
            userRoleLabel.setText(role);
            userRoleLabel.getStyleClass().clear();
            if ("ADMIN".equals(role)) {
                userRoleLabel.getStyleClass().add("role-badge-admin");
            } else {
                userRoleLabel.getStyleClass().add("role-badge-warga");
            }
        }

        if (!UserSession.isAdmin()) {
            if (btnKecamatan != null) btnKecamatan.setManaged(false);
            if (btnKecamatan != null) btnKecamatan.setVisible(false);
            if (btnDesa != null) btnDesa.setManaged(false);
            if (btnDesa != null) btnDesa.setVisible(false);
            if (btnWarga != null) btnWarga.setManaged(false);
            if (btnWarga != null) btnWarga.setVisible(false);
            if (btnLaporan != null) btnLaporan.setManaged(false);
            if (btnLaporan != null) btnLaporan.setVisible(false);
        }
    }

    private void loadProfileData() {
        String sql = "SELECT w.*, d.nama as desa_nama FROM warga w JOIN desa d ON w.desa_id = d.id WHERE w.nik = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, UserSession.getNik());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                nikField.setText(rs.getString("nik"));
                namaField.setText(rs.getString("nama"));
                jenkelField.setText(rs.getString("jenis_kelamin"));
                tglLahirField.setText(rs.getString("tanggal_lahir"));
                roleField.setText(UserSession.getRole());
                desaField.setText(rs.getString("desa_nama"));
                rtrwField.setText(rs.getString("rt") + " / " + rs.getString("rw"));
                alamatArea.setText(rs.getString("alamat"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML private void goToDashboard() throws IOException { App.setRoot("dashboard", 1200, 800, true); }
    @FXML private void goToKecamatan() throws IOException { App.setRoot("kecamatan", 1200, 800, true); }
    @FXML private void goToDesa() throws IOException { App.setRoot("desa", 1200, 800, true); }
    @FXML private void goToWarga() throws IOException { App.setRoot("warga", 1200, 800, true); }
    @FXML private void goToLaporan() throws IOException { App.setRoot("laporan", 1200, 800, true); }
    
    @FXML
    private void handleLogout() throws IOException {
        UserSession.logout();
        App.setRoot("login", 800, 450);
    }
}
