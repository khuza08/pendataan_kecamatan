// src/pendataankecamatan/controller/PublicController.java
package pendataankecamatan.controller;

import pendataankecamatan.service.DatabaseService;
import pendataankecamatan.model.Desa;
import pendataankecamatan.model.Pejabat;
import java.util.List;

public class PublicController {
    private final DatabaseService dbService = new DatabaseService();

    public List<Desa> getDaftarDesa() {
        return dbService.getAllDesa();
    }

    public List<Pejabat> getDaftarPejabat() {
        return dbService.getAllPejabat();
    }

    public String getProfilKecamatan() {
        return """
            KECAMATAN SIWALAN PANJI\n
            Kabupaten: Sidoarjo\n
            Provinsi: Jawa Timur\n
            Luas Wilayah: ±18,4 km²\n
            Jumlah Penduduk: ±66.300 jiwa\n
            Jumlah Desa: 5\n
            \n
            Visi:\n
            "Terwujudnya Kecamatan Siwalan Panji yang Maju, Mandiri, dan Berdaya Saing"
            """;
    }
}