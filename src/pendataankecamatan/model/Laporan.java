package pendataankecamatan.model;

import java.time.LocalDateTime;

public class Laporan {
    private int id;
    private int userId;
    private String judul;
    private String deskripsi;
    private String status; // DIPROSES, SELESAI, DITOLAK
    private LocalDateTime createdAt;

    // Constructor
    public Laporan(int id, int userId, String judul, String deskripsi, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
