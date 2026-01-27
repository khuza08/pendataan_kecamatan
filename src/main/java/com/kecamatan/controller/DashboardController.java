package com.kecamatan.controller;

import com.kecamatan.App;
import javafx.fxml.FXML;
import java.io.IOException;

public class DashboardController {

    @FXML
    private void handleLogout() throws IOException {
        App.setRoot("login", 400, 500);
    }
}
