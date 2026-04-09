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

import com.kecamatan.util.RBACUtil;
import com.kecamatan.util.UIUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.text.Text;

public class ProfilController implements Initializable {

    @FXML private TextField nikField;
    @FXML private TextField namaField;
    @FXML private TextField jenkelField;
    @FXML private TextField tglLahirField;
    @FXML private TextField agamaField;
    @FXML private TextField statusKawinField;
    @FXML private TextField pekerjaanField;
    @FXML private TextField noHpField;
    @FXML private TextField desaField;
    @FXML private TextField kecamatanField;
    @FXML private TextField rtrwField;
    @FXML private TextArea alamatArea;

    @FXML private Button btnDesa;
    @FXML private Button btnWarga;
    @FXML private Button btnDashboard;
    @FXML private Button btnLaporan;
    @FXML private Button btnKepalaDesa;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Text clockTimeText;
    @FXML private Text clockDateText;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        applyRBAC();
        startClock();
        loadProfileData();
    }

    private void startClock() {
        if (clockTimeText != null && clockDateText != null) {
            Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
                LocalTime time = LocalTime.now();
                clockTimeText.setText(time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                LocalDate date = LocalDate.now();
                String dayName = date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.forLanguageTag("id"));
                String monthName = date.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.forLanguageTag("id"));
                clockDateText.setText(String.format("%s, %d %s %d", dayName, date.getDayOfMonth(), monthName, date.getYear()));
            }), new KeyFrame(Duration.seconds(1)));
            clock.setCycleCount(Timeline.INDEFINITE);
            clock.play();
        }
    }

    private void applyRBAC() {
        RBACUtil.applyRBAC(userNameLabel, userRoleLabel, 
            btnDesa, btnWarga, btnLaporan, btnDashboard);
    }

    private void loadProfileData() {
        String sql = "SELECT w.*, d.nama as desa_nama, k.nama as kecamatan_nama " +
                     "FROM warga w JOIN desa d ON w.desa_id = d.id JOIN kecamatan k ON d.kecamatan_id = k.id " +
                     "WHERE w.nik = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, UserSession.getNik());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                nikField.setText(rs.getString("nik"));
                namaField.setText(rs.getString("nama"));
                jenkelField.setText(rs.getString("jenis_kelamin"));
                tglLahirField.setText(rs.getString("tanggal_lahir"));
                agamaField.setText(rs.getString("agama"));
                statusKawinField.setText(rs.getString("status_kawin"));
                pekerjaanField.setText(rs.getString("pekerjaan"));
                noHpField.setText(rs.getString("no_hp"));
                desaField.setText(rs.getString("desa_nama"));
                kecamatanField.setText(rs.getString("kecamatan_nama"));
                String rt = rs.getString("rt");
                String rw = rs.getString("rw");
                rtrwField.setText((rt != null ? rt : "-") + " / " + (rw != null ? rw : "-"));
                alamatArea.setText(rs.getString("alamat"));
            }
        } catch (SQLException e) {
            UIUtil.showDatabaseError("Memuat Data Profil", e);
        }
    }

    @FXML private void goToDashboard() throws IOException { App.setRoot("dashboard", 1200, 800, true); }
    @FXML private void goToDesa() throws IOException { App.setRoot("desa", 1200, 800, true); }
    @FXML private void goToWarga() throws IOException { App.setRoot("warga", 1200, 800, true); }
    @FXML private void goToLaporan() throws IOException { App.setRoot("laporan", 1200, 800, true); }

    @FXML
    private void goToKepalaDesa() throws IOException {
        if (UserSession.isAdmin()) {
            App.setRoot("kepala_desa", 1200, 800, true);
        } else {
            App.setRoot("kepala_desa_warga", 1200, 800, true);
        }
    }
    
    @FXML
    private void handleLogout() throws IOException {
        UserSession.logout();
        App.setRoot("login", 800, 450);
    }
}
