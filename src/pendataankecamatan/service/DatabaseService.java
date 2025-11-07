// src/pendataankecamatan/service/DatabaseService.java
package pendataankecamatan.service;

import pendataankecamatan.model.User;
import java.sql.*;

public class DatabaseService {

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