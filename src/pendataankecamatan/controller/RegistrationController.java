// src/pendataankecamatan/controller/RegistrationController.java
package pendataankecamatan.controller;

import pendataankecamatan.model.Warga;
import pendataankecamatan.service.DatabaseService;
import pendataankecamatan.util.Constants;

public class RegistrationController {
    private final DatabaseService dbService = new DatabaseService();

    public boolean updateProfile(Warga warga) {
        System.out.println("Data warga diperbarui: " + warga.getNik());
        return dbService.updateWarga(warga);
    }

    public Warga getProfile(int userId) {
        return dbService.getWargaByUserId(userId);
    }
}