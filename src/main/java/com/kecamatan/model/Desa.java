package com.kecamatan.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Desa {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty kecamatanId;
    private final SimpleStringProperty kecamatanNama;
    private final SimpleStringProperty nama;
    private final SimpleIntegerProperty populasi;
    private final SimpleIntegerProperty jumlahRt;
    private final SimpleIntegerProperty jumlahRw;
    private final SimpleStringProperty kepalaDesaNama;
    private final SimpleStringProperty kodePos;
    private final SimpleStringProperty alamat;

    public Desa(int id, int kecamatanId, String kecamatanNama, String nama, int populasi, int jumlahRt, int jumlahRw) {
        this(id, kecamatanId, kecamatanNama, nama, populasi, jumlahRt, jumlahRw, "", "", "");
    }

    public Desa(int id, int kecamatanId, String kecamatanNama, String nama, int populasi, int jumlahRt, int jumlahRw, String kepalaDesaNama) {
        this(id, kecamatanId, kecamatanNama, nama, populasi, jumlahRt, jumlahRw, kepalaDesaNama, "", "");
    }

    public Desa(int id, int kecamatanId, String kecamatanNama, String nama, int populasi, int jumlahRt, int jumlahRw, String kepalaDesaNama, String kodePos, String alamat) {
        this.id = new SimpleIntegerProperty(id);
        this.kecamatanId = new SimpleIntegerProperty(kecamatanId);
        this.kecamatanNama = new SimpleStringProperty(kecamatanNama);
        this.nama = new SimpleStringProperty(nama);
        this.populasi = new SimpleIntegerProperty(populasi);
        this.jumlahRt = new SimpleIntegerProperty(jumlahRt);
        this.jumlahRw = new SimpleIntegerProperty(jumlahRw);
        this.kepalaDesaNama = new SimpleStringProperty(kepalaDesaNama);
        this.kodePos = new SimpleStringProperty(kodePos);
        this.alamat = new SimpleStringProperty(alamat);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }

    public int getKecamatanId() { return kecamatanId.get(); }
    public SimpleIntegerProperty kecamatanIdProperty() { return kecamatanId; }

    public String getKecamatanNama() { return kecamatanNama.get(); }
    public SimpleStringProperty kecamatanNamaProperty() { return kecamatanNama; }

    public String getNama() { return nama.get(); }
    public SimpleStringProperty namaProperty() { return nama; }

    public int getPopulasi() { return populasi.get(); }
    public SimpleIntegerProperty populasiProperty() { return populasi; }

    public int getJumlahRt() { return jumlahRt.get(); }
    public SimpleIntegerProperty jumlahRtProperty() { return jumlahRt; }

    public int getJumlahRw() { return jumlahRw.get(); }
    public SimpleIntegerProperty jumlahRwProperty() { return jumlahRw; }

    public String getKepalaDesaNama() { return kepalaDesaNama.get(); }
    public SimpleStringProperty kepalaDesaNamaProperty() { return kepalaDesaNama; }

    public String getKodePos() { return kodePos.get(); }
    public SimpleStringProperty kodePosProperty() { return kodePos; }

    public String getAlamat() { return alamat.get(); }
    public SimpleStringProperty alamatProperty() { return alamat; }
}
