package com.kecamatan.controller;

import com.kecamatan.App;
import com.kecamatan.model.KepalaDesa;
import com.kecamatan.util.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.kecamatan.util.RBACUtil;
import com.kecamatan.util.UIUtil;
import com.kecamatan.util.UserSession;

public class KepalaDesaWargaController implements Initializable {

    @FXML private TableView<KepalaDesa> kepalaDesaTable;
    @FXML private TableColumn<KepalaDesa, String> colNama;
    @FXML private TableColumn<KepalaDesa, String> colDesa;
    @FXML private TableColumn<KepalaDesa, LocalDate> colMulai;
    @FXML private TableColumn<KepalaDesa, LocalDate> colSelesai;
    @FXML private TableColumn<KepalaDesa, String> colStatus;

    @FXML private Button btnKepalaDesa;
    @FXML private Button btnProfil;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;

    private ObservableList<KepalaDesa> kepalaDesaList = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup user info
        RBACUtil.setupUserLabels(userNameLabel, userRoleLabel);

        // Setup table columns
        colNama.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        colDesa.setCellValueFactory(cellData -> cellData.getValue().desaNamaProperty());
        colMulai.setCellValueFactory(cellData -> cellData.getValue().periodeMulaiProperty());
        colSelesai.setCellValueFactory(cellData -> cellData.getValue().periodeSelesaiProperty());
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        // Date formatters
        colMulai.setCellFactory(column -> new TableCell<KepalaDesa, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatter.format(item));
            }
        });
        colSelesai.setCellFactory(column -> new TableCell<KepalaDesa, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatter.format(item));
            }
        });

        // Status styling
        colStatus.setCellFactory(column -> new TableCell<KepalaDesa, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Aktif".equals(item)) {
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    } else if ("Berakhir".equals(item)) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        loadData();
    }

    private void loadData() {
        com.kecamatan.util.ThreadManager.execute(() -> {
            ObservableList<KepalaDesa> tempData = FXCollections.observableArrayList();
            String sql = "SELECT kd.*, d.nama as desa_nama FROM kepala_desa kd " +
                         "JOIN desa d ON kd.desa_id = d.id ORDER BY kd.nama";

            try (Connection conn = DatabaseUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    LocalDate periodeSelesai = rs.getString("periode_selesai") != null ? LocalDate.parse(rs.getString("periode_selesai")) : null;
                    String status = (periodeSelesai != null && LocalDate.now().isAfter(periodeSelesai)) ? "Berakhir" : "Aktif";
                    
                    tempData.add(new KepalaDesa(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getInt("desa_id"),
                        rs.getString("desa_nama"),
                        rs.getString("periode_mulai") != null ? LocalDate.parse(rs.getString("periode_mulai")) : null,
                        periodeSelesai,
                        status
                    ));
                }
                
                javafx.application.Platform.runLater(() -> {
                    kepalaDesaList.setAll(tempData);
                    kepalaDesaTable.setItems(kepalaDesaList);
                });
            } catch (SQLException e) {
                UIUtil.showDatabaseError("Memuat Data Kepala Desa", e);
            }
        });
    }

    @FXML
    private void goToProfil() throws IOException {
        App.setRoot("profil", 1200, 800, true);
    }

    @FXML
    private void handleLogout() throws IOException {
        UserSession.logout();
        App.setRoot("login", 800, 450);
    }
}
