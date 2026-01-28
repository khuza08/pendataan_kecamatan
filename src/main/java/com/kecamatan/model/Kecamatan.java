package com.kecamatan.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Kecamatan {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty kode;
    private final SimpleStringProperty nama;
    private final SimpleIntegerProperty jumlahDesa;
    private final SimpleIntegerProperty populasi;

    public Kecamatan(int id, String kode, String nama, int jumlahDesa, int populasi) {
        this.id = new SimpleIntegerProperty(id);
        this.kode = new SimpleStringProperty(kode);
        this.nama = new SimpleStringProperty(nama);
        this.jumlahDesa = new SimpleIntegerProperty(jumlahDesa);
        this.populasi = new SimpleIntegerProperty(populasi);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }

    public String getKode() { return kode.get(); }
    public SimpleStringProperty kodeProperty() { return kode; }

    public String getNama() { return nama.get(); }
    public SimpleStringProperty namaProperty() { return nama; }

    public int getJumlahDesa() { return jumlahDesa.get(); }
    public SimpleIntegerProperty jumlahDesaProperty() { return jumlahDesa; }

    public int getPopulasi() { return populasi.get(); }
    public SimpleIntegerProperty populasiProperty() { return populasi; }
}
