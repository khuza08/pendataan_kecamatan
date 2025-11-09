// src/pendataankecamatan/service/DatabaseService.java
package pendataankecamatan.service;

import pendataankecamatan.model.User;
import pendataankecamatan.model.Desa;
import pendataankecamatan.model.Pejabat;
import pendataankecamatan.model.Warga;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class DatabaseService {
    
    // Ambil semua desa
    public List<Desa> getAllDesa() {
        List<Desa> desaList = new ArrayList<>();
        String sql = "SELECT id, nama, kode_pos, luas_wilayah, jumlah_penduduk FROM desa ORDER BY nama";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return desaList;
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Desa desa = new Desa(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("kode_pos")
                );
                // Set properti tambahan jika diperlukan
                desaList.add(desa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return desaList;
    }

    // Ambil semua pejabat
    public List<Pejabat> getAllPejabat() {
        List<Pejabat> pejabatList = new ArrayList<>();
        String sql = "SELECT id, nama, jabatan, nomor_telepon, email FROM pejabat ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return pejabatList;
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Pejabat pejabat = new Pejabat(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("jabatan"),
                    rs.getString("nomor_telepon")
                );
                // Set email jika perlu
                pejabatList.add(pejabat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pejabatList;
    }

    public User authenticateUser(String username, String password) {
        String sql = "SELECT id, username, nama_lengkap, role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (conn == null) return null;

            stmt.setString(1, username);
            stmt.setString(2, password); 

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("nama_lengkap"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateWarga(Warga warga) {
        String sql = "INSERT INTO warga (user_id, nik, nama_lengkap, alamat, rt, rw, desa_id, jenis_kelamin, tanggal_lahir) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE nik=?, alamat=?, rt=?, rw=?, desa_id=?, jenis_kelamin=?, tanggal_lahir=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, warga.getUserId());
            stmt.setString(2, warga.getNik());
            stmt.setString(3, warga.getNamaLengkap());
            stmt.setString(4, warga.getAlamat());
            stmt.setString(5, warga.getRt());
            stmt.setString(6, warga.getRw());
            stmt.setInt(7, warga.getDesaId());
            stmt.setString(8, warga.getJenisKelamin());
            stmt.setString(9, warga.getTanggalLahir());

            // Update values
            stmt.setString(10, warga.getNik());
            stmt.setString(11, warga.getAlamat());
            stmt.setString(12, warga.getRt());
            stmt.setString(13, warga.getRw());
            stmt.setInt(14, warga.getDesaId());
            stmt.setString(15, warga.getJenisKelamin());
            stmt.setString(16, warga.getTanggalLahir());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Warga getWargaByUserId(int userId) {
        String sql = "SELECT * FROM warga WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Warga(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("nik"),
                    rs.getString("nama_lengkap"),
                    rs.getString("alamat"),
                    rs.getString("rt"),
                    rs.getString("rw"),
                    rs.getInt("desa_id"),
                    rs.getString("jenis_kelamin"),
                    rs.getString("tanggal_lahir")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean saveLaporan(int userId, String judul, String deskripsi) {
        String sql = "INSERT INTO laporan (user_id, judul, deskripsi) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return false;
            stmt.setInt(1, userId);
            stmt.setString(2, judul);
            stmt.setString(3, deskripsi);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}