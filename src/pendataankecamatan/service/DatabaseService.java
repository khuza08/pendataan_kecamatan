package pendataankecamatan.service;

import pendataankecamatan.model.User;

public class DatabaseService {
    public User authenticateUser(String username, String password) {
        // Demo: hardcode sementara
        if ("admin".equals(username) && "password".equals(password)) {
            return new User(1, "admin", "Administrator Kecamatan", "ADMIN");
        }
        return null;
    }

    public boolean save(Object entity) {
        System.out.println("Simulasi menyimpan: " + entity.getClass().getSimpleName());
        return true;
    }
}
