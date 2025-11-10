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
    
    // ==================== DESA CRUD ====================
    
    public boolean deleteDesa(int id) {
        String sql = "DELETE FROM desa WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateDesa(Desa desa) {
        String sql = "INSERT INTO desa (id, nama, kode_pos) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE nama=?, kode_pos=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, desa.getId());
            stmt.setString(2, desa.getNama());
            stmt.setString(3, desa.getKodePos());

            stmt.setString(4, desa.getNama());
            stmt.setString(5, desa.getKodePos());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Desa> getAllDesa() {
        List<Desa> list = new ArrayList<>();
        String sql = "SELECT * FROM desa ORDER BY nama";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Desa(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("kode_pos")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Desa getDesaById(int id) {
        String sql = "SELECT * FROM desa WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Desa(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("kode_pos")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==================== WARGA CRUD ====================

    public boolean deleteWarga(int id) {
        String sql = "DELETE FROM warga WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Warga> getAllWarga() {
        List<Warga> list = new ArrayList<>();
        String sql = "SELECT * FROM warga ORDER BY nama_lengkap";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Warga(
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
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Warga getWargaById(int id) {
        String sql = "SELECT * FROM warga WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
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

    public boolean updateWarga(Warga warga) {
        String sql = "INSERT INTO warga (id, user_id, nik, nama_lengkap, alamat, rt, rw, desa_id, jenis_kelamin, tanggal_lahir) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE user_id=?, nik=?, nama_lengkap=?, alamat=?, rt=?, rw=?, desa_id=?, jenis_kelamin=?, tanggal_lahir=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, warga.getId());
            stmt.setInt(2, warga.getUserId());
            stmt.setString(3, warga.getNik());
            stmt.setString(4, warga.getNamaLengkap());
            stmt.setString(5, warga.getAlamat());
            stmt.setString(6, warga.getRt());
            stmt.setString(7, warga.getRw());
            stmt.setInt(8, warga.getDesaId());
            stmt.setString(9, warga.getJenisKelamin());
            stmt.setString(10, warga.getTanggalLahir());

            // Update values
            stmt.setInt(11, warga.getUserId());
            stmt.setString(12, warga.getNik());
            stmt.setString(13, warga.getNamaLengkap());
            stmt.setString(14, warga.getAlamat());
            stmt.setString(15, warga.getRt());
            stmt.setString(16, warga.getRw());
            stmt.setInt(17, warga.getDesaId());
            stmt.setString(18, warga.getJenisKelamin());
            stmt.setString(19, warga.getTanggalLahir());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Ambil warga berdasarkan user_id (untuk form registrasi)
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

    // ==================== PEJABAT CRUD ====================

    public boolean deletePejabat(int id) {
        String sql = "DELETE FROM pejabat WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Pejabat> getAllPejabat() {
        List<Pejabat> list = new ArrayList<>();
        String sql = "SELECT * FROM pejabat ORDER BY nama";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Pejabat(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("jabatan"),
                    rs.getString("nomor_telepon")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Pejabat getPejabatById(int id) {
        String sql = "SELECT * FROM pejabat WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Pejabat(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("jabatan"),
                    rs.getString("nomor_telepon")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePejabat(Pejabat pejabat) {
        String sql = "INSERT INTO pejabat (id, nama, jabatan, nomor_telepon) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE nama=?, jabatan=?, nomor_telepon=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pejabat.getId());
            stmt.setString(2, pejabat.getNama());
            stmt.setString(3, pejabat.getJabatan());
            stmt.setString(4, pejabat.getNomorTelepon());

            // Update values
            stmt.setString(5, pejabat.getNama());
            stmt.setString(6, pejabat.getJabatan());
            stmt.setString(7, pejabat.getNomorTelepon());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== USER AUTHENTICATION ====================

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

    // ==================== LAPORAN ====================

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