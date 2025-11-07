package pendataankecamatan.controller;

import pendataankecamatan.model.User;
import pendataankecamatan.service.DatabaseService;
import pendataankecamatan.util.Constants;

public class AuthController {
    private final DatabaseService dbService = new DatabaseService();

    public boolean login(String username, String password) {
        User user = dbService.authenticateUser(username, password);
        if (user != null) {
            Constants.CURRENT_USER = user;
            return true;
        }
        return false;
    }

    public void logout() {
        Constants.CURRENT_USER = null;
    }
}
