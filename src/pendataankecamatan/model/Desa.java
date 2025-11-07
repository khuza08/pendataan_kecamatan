package pendataankecamatan.model;

public class Desa {
    private int id;
    private String nama;
    private String kodePos;

    // Constructor
    public Desa(int id, String nama, String kodePos) {
        this.id = id;
        this.nama = nama;
        this.kodePos = kodePos;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getKodePos() { return kodePos; }
    public void setKodePos(String kodePos) { this.kodePos = kodePos; }
}
