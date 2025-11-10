// src/pendataankecamatan/model/Pejabat.java
package pendataankecamatan.model;

public class Pejabat {
    private int id;
    private String nama;
    private String jabatan;
    private String nomorTelepon;

    // Constructor
    public Pejabat(int id, String nama, String jabatan, String nomorTelepon) {
        this.id = id;
        this.nama = nama;
        this.jabatan = jabatan;
        this.nomorTelepon = nomorTelepon;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getJabatan() { return jabatan; }
    public void setJabatan(String jabatan) { this.jabatan = jabatan; }

    public String getNomorTelepon() { return nomorTelepon; }
    public void setNomorTelepon(String nomorTelepon) { this.nomorTelepon = nomorTelepon; }

    @Override
    public String toString() {
        return "Pejabat{" +
               "id=" + id +
               ", nama='" + nama + '\'' +
               ", jabatan='" + jabatan + '\'' +
               ", nomorTelepon='" + nomorTelepon + '\'' +
               '}';
    }
}