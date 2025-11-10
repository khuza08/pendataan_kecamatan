// src/pendataankecamatan/model/Warga.java
package pendataankecamatan.model;

public class Warga {
    private int id;
    private int userId;
    private String nik;
    private String namaLengkap;
    private String alamat;
    private String rt;
    private String rw;
    private int desaId;
    private String jenisKelamin;
    private String tanggalLahir;

    // Constructor
    public Warga(int id, int userId, String nik, String namaLengkap, String alamat, String rt, String rw, int desaId, String jenisKelamin, String tanggalLahir) {
        this.id = id;
        this.userId = userId;
        this.nik = nik;
        this.namaLengkap = namaLengkap;
        this.alamat = alamat;
        this.rt = rt;
        this.rw = rw;
        this.desaId = desaId;
        this.jenisKelamin = jenisKelamin;
        this.tanggalLahir = tanggalLahir;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }

    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getRt() { return rt; }
    public void setRt(String rt) { this.rt = rt; }

    public String getRw() { return rw; }
    public void setRw(String rw) { this.rw = rw; }

    public int getDesaId() { return desaId; }
    public void setDesaId(int desaId) { this.desaId = desaId; }

    public String getJenisKelamin() { return jenisKelamin; }
    public void setJenisKelamin(String jenisKelamin) { this.jenisKelamin = jenisKelamin; }

    public String getTanggalLahir() { return tanggalLahir; }
    public void setTanggalLahir(String tanggalLahir) { this.tanggalLahir = tanggalLahir; }

    @Override
    public String toString() {
        return "Warga{" +
               "id=" + id +
               ", userId=" + userId +
               ", nik='" + nik + '\'' +
               ", namaLengkap='" + namaLengkap + '\'' +
               ", alamat='" + alamat + '\'' +
               ", rt='" + rt + '\'' +
               ", rw='" + rw + '\'' +
               ", desaId=" + desaId +
               ", jenisKelamin='" + jenisKelamin + '\'' +
               ", tanggalLahir='" + tanggalLahir + '\'' +
               '}';
    }
}