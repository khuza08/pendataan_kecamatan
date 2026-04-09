package com.kecamatan.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.time.LocalDate;

public class KepalaDesa {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nama;
    private final SimpleIntegerProperty desaId;
    private final SimpleStringProperty desaNama;
    private final SimpleObjectProperty<LocalDate> periodeMulai;
    private final SimpleObjectProperty<LocalDate> periodeSelesai;
    private final SimpleStringProperty status;

    public KepalaDesa(int id, String nama, int desaId, String desaNama, LocalDate periodeMulai, LocalDate periodeSelesai, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.nama = new SimpleStringProperty(nama);
        this.desaId = new SimpleIntegerProperty(desaId);
        this.desaNama = new SimpleStringProperty(desaNama);
        this.periodeMulai = new SimpleObjectProperty<>(periodeMulai);
        this.periodeSelesai = new SimpleObjectProperty<>(periodeSelesai);
        this.status = new SimpleStringProperty(status);
    }

    public int getId() { return id.get(); }
    public SimpleIntegerProperty idProperty() { return id; }

    public String getNama() { return nama.get(); }
    public SimpleStringProperty namaProperty() { return nama; }

    public int getDesaId() { return desaId.get(); }
    public SimpleIntegerProperty desaIdProperty() { return desaId; }

    public String getDesaNama() { return desaNama.get(); }
    public SimpleStringProperty desaNamaProperty() { return desaNama; }

    public LocalDate getPeriodeMulai() { return periodeMulai.get(); }
    public SimpleObjectProperty<LocalDate> periodeMulaiProperty() { return periodeMulai; }

    public LocalDate getPeriodeSelesai() { return periodeSelesai.get(); }
    public SimpleObjectProperty<LocalDate> periodeSelesaiProperty() { return periodeSelesai; }

    public String getStatus() { return status.get(); }
    public SimpleStringProperty statusProperty() { return status; }
}
