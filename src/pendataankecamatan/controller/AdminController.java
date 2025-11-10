// src/pendataankecamatan/controller/AdminController.java
package pendataankecamatan.controller;

import pendataankecamatan.service.DatabaseService;
import pendataankecamatan.model.Desa;
import pendataankecamatan.model.Warga;
import pendataankecamatan.model.Pejabat;

import java.util.List;

public class AdminController {
    private final DatabaseService dbService = new DatabaseService();

    public void manageData() {
        System.out.println("Admin mengelola data desa, warga, dan pejabat.");
    }

    // Desa
    public boolean updateDesa(Desa desa) {
        return dbService.updateDesa(desa);
    }

    public boolean deleteDesa(int id) {
        return dbService.deleteDesa(id);
    }

    public List<Desa> getAllDesa() {
        return dbService.getAllDesa();
    }

    public Desa getDesaById(int id) {
        return dbService.getDesaById(id);
    }

    // Warga
    public boolean updateWarga(Warga warga) {
        return dbService.updateWarga(warga);
    }

    public boolean deleteWarga(int id) {
        return dbService.deleteWarga(id);
    }

    public List<Warga> getAllWarga() {
        return dbService.getAllWarga();
    }

    public Warga getWargaById(int id) {
        return dbService.getWargaById(id);
    }

    // Pejabat
    public boolean updatePejabat(Pejabat pejabat) {
        return dbService.updatePejabat(pejabat);
    }

    public boolean deletePejabat(int id) {
        return dbService.deletePejabat(id);
    }

    public List<Pejabat> getAllPejabat() {
        return dbService.getAllPejabat();
    }

    public Pejabat getPejabatById(int id) {
        return dbService.getPejabatById(id);
    }
}