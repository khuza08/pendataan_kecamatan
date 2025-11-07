package pendataankecamatan.controller;

import pendataankecamatan.model.Warga;

public class RegistrationController {
    public boolean updateProfile(Warga warga) {
        // Simulasikan update data warga
        System.out.println("Data warga diperbarui: " + warga.getNik());
        return true;
    }
}
