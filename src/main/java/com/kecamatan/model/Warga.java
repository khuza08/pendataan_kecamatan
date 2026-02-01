package com.kecamatan.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.LocalDate;

public class Warga {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nik;
    private final SimpleStringProperty nama;
    private final SimpleStringProperty alamat;
    private final SimpleStringProperty jenisKelamin;
    private final SimpleIntegerProperty desaId;
    private final SimpleStringProperty desaNama;
    private final SimpleStringProperty rt;
    private final SimpleStringProperty rw;
    private final SimpleObjectProperty<LocalDate> tanggalLahir;

    public Warga(int id, String nik, String nama, String alamat, String jenisKelamin, int desaId, String desaNama, String rt, String rw, LocalDate tanggalLahir) {
        this.id = new SimpleIntegerProperty(id);
        this.nik = new SimpleStringProperty(nik);
        this.nama = new SimpleStringProperty(nama);
        this.alamat = new SimpleStringProperty(alamat);
        this.jenisKelamin = new SimpleStringProperty(jenisKelamin);
        this.desaId = new SimpleIntegerProperty(desaId);
        this.desaNama = new SimpleStringProperty(desaNama);
        this.rt = new SimpleStringProperty(rt);
        this.rw = new SimpleStringProperty(rw);
        this.tanggalLahir = new SimpleObjectProperty<>(tanggalLahir);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }

    public String getNik() { return nik.get(); }
    public SimpleStringProperty nikProperty() { return nik; }

    public String getNama() { return nama.get(); }
    public SimpleStringProperty namaProperty() { return nama; }

    public String getAlamat() { return alamat.get(); }
    public SimpleStringProperty alamatProperty() { return alamat; }

    public String getJenisKelamin() { return jenisKelamin.get(); }
    public SimpleStringProperty jenisKelaminProperty() { return jenisKelamin; }

    public int getDesaId() { return desaId.get(); }
    public SimpleIntegerProperty desaIdProperty() { return desaId; }

    public String getDesaNama() { return desaNama.get(); }
    public SimpleStringProperty desaNamaProperty() { return desaNama; }

    public String getRt() { return rt.get(); }
    public SimpleStringProperty rtProperty() { return rt; }

    public String getRw() { return rw.get(); }
    public SimpleStringProperty rwProperty() { return rw; }

    public LocalDate getTanggalLahir() { return tanggalLahir.get(); }
    public SimpleObjectProperty<LocalDate> tanggalLahirProperty() { return tanggalLahir; }
}
