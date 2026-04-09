package com.kecamatan.controller;

import com.kecamatan.App;
import com.kecamatan.model.Desa;
import com.kecamatan.model.KepalaDesa;
import com.kecamatan.util.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.kecamatan.util.DataRefreshable;
import com.kecamatan.util.RBACUtil;
import com.kecamatan.util.UIUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.text.Text;

public class KepalaDesaController implements Initializable, DataRefreshable {

    @Override
    public void refreshData() {
        loadDesaData();
        loadData();
    }

    @FXML private TableView<KepalaDesa> kepalaDesaTable;
    @FXML private TableColumn<KepalaDesa, String> colNama;
    @FXML private TableColumn<KepalaDesa, String> colDesa;
    @FXML private TableColumn<KepalaDesa, LocalDate> colMulai;
    @FXML private TableColumn<KepalaDesa, LocalDate> colSelesai;
    @FXML private TableColumn<KepalaDesa, String> colStatus;

    @FXML private TextField namaField;
    @FXML private ComboBox<Desa> desaComboBox;
    @FXML private DatePicker periodeMulaiPicker;
    @FXML private DatePicker periodeSelesaiPicker;
    @FXML private TextField searchField;

    @FXML private Button btnDesa;
    @FXML private Button btnKepalaDesa;
    @FXML private Button btnWarga;
    @FXML private Button btnDashboard;
    @FXML private Button btnLaporan;
    @FXML private Button btnProfil;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Text clockTimeText;
    @FXML private Text clockDateText;

    private ObservableList<KepalaDesa> kepalaDesaList = FXCollections.observableArrayList();
    private ObservableList<Desa> desaList = FXCollections.observableArrayList();
    private int selectedId = -1;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        applyRBAC();
        colNama.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        colDesa.setCellValueFactory(cellData -> cellData.getValue().desaNamaProperty());
        colMulai.setCellValueFactory(cellData -> cellData.getValue().periodeMulaiProperty());
        colSelesai.setCellValueFactory(cellData -> cellData.getValue().periodeSelesaiProperty());

        colMulai.setCellFactory(column -> {
            return new TableCell<KepalaDesa, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            };
        });
        colSelesai.setCellFactory(column -> {
            return new TableCell<KepalaDesa, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(item));
                    }
                }
            };
        });
        colStatus.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        colStatus.setCellFactory(column -> new TableCell<KepalaDesa, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
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

        setupDatePicker();
        setupDesaComboBox();
        loadDesaData();
        loadData();

        kepalaDesaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedId = newSel.getId();
                namaField.setText(newSel.getNama());
                periodeMulaiPicker.setValue(newSel.getPeriodeMulai());
                periodeSelesaiPicker.setValue(newSel.getPeriodeSelesai());
                for (Desa d : desaList) {
                    if (d.getId() == newSel.getDesaId()) {
                        desaComboBox.getSelectionModel().select(d);
                        break;
                    }
                }
            }
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            loadData();
        });

        // Nama input filter: letters, spaces, dots, commas only
        namaField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("[a-zA-Z\\s.,]*")) {
                namaField.setText(oldVal);
                namaField.positionCaret(namaField.getCaretPosition() - 1);
            }
        });

        startClock();
    }

    private void startClock() {
        if (clockTimeText != null && clockDateText != null) {
            Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
                LocalTime time = LocalTime.now();
                clockTimeText.setText(time.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                LocalDate date = LocalDate.now();
                String dayName = date.getDayOfWeek().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.forLanguageTag("id"));
                String monthName = date.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.forLanguageTag("id"));
                clockDateText.setText(String.format("%s, %d %s %d", dayName, date.getDayOfMonth(), monthName, date.getYear()));
            }), new KeyFrame(Duration.seconds(1)));
            clock.setCycleCount(Timeline.INDEFINITE);
            clock.play();
        }
    }

    private void applyRBAC() {
        RBACUtil.applyFullRBAC(userNameLabel, userRoleLabel, btnProfil,
            btnDesa, btnWarga, btnLaporan, btnDashboard);
    }

    private void setupDatePicker() {
        periodeMulaiPicker.setEditable(false);
        periodeMulaiPicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try { return LocalDate.parse(string, formatter); } catch (Exception e) { return null; }
                }
                return null;
            }
        });
        periodeSelesaiPicker.setEditable(false);
        periodeSelesaiPicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try { return LocalDate.parse(string, formatter); } catch (Exception e) { return null; }
                }
                return null;
            }
        });
    }

    private void setupDesaComboBox() {
        desaComboBox.setConverter(new StringConverter<Desa>() {
            @Override
            public String toString(Desa d) {
                return d == null ? "" : d.getNama();
            }
            @Override
            public Desa fromString(String string) { return null; }
        });
    }

    private void loadDesaData() {
        com.kecamatan.util.ThreadManager.execute(() -> {
            ObservableList<Desa> tempDesa = FXCollections.observableArrayList();
            String sql = "SELECT d.*, k.nama as kecamatan_nama " +
                         "FROM desa d JOIN kecamatan k ON d.kecamatan_id = k.id " +
                         "WHERE d.kecamatan_id = (SELECT id FROM kecamatan WHERE nama = 'Siwalanpanji') ORDER BY d.nama";
            try (Connection conn = DatabaseUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    tempDesa.add(new Desa(
                        rs.getInt("id"),
                        rs.getInt("kecamatan_id"),
                        rs.getString("kecamatan_nama"),
                        rs.getString("nama"),
                        0, 0, 0
                    ));
                }
                javafx.application.Platform.runLater(() -> {
                    desaList.setAll(tempDesa);
                    desaComboBox.setItems(desaList);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadData() {
        com.kecamatan.util.ThreadManager.execute(() -> {
            ObservableList<KepalaDesa> tempData = FXCollections.observableArrayList();
            String searchQuery = searchField != null ? searchField.getText() : null;
            boolean hasSearch = searchQuery != null && !searchQuery.trim().isEmpty();

            StringBuilder sql = new StringBuilder(
                "SELECT kd.id, kd.nama, kd.desa_id, kd.periode_mulai, kd.periode_selesai, d.nama as desa_nama FROM kepala_desa kd " +
                "JOIN desa d ON kd.desa_id = d.id"
            );
            if (hasSearch) {
                sql.append(" WHERE LOWER(kd.nama) LIKE LOWER(?)");
            }
            sql.append(" ORDER BY kd.nama");

            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                if (hasSearch) {
                    pstmt.setString(1, "%" + searchQuery.trim() + "%");
                }
                ResultSet rs = pstmt.executeQuery();
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
    private void handleSave() {
        String nama = namaField.getText();
        Desa selectedDesa = desaComboBox.getSelectionModel().getSelectedItem();
        LocalDate periodeMulai = periodeMulaiPicker.getValue();
        LocalDate periodeSelesai = periodeSelesaiPicker.getValue();

        com.kecamatan.util.UIUtil.setErrorStyle(namaField, false);
        com.kecamatan.util.UIUtil.setErrorStyle(desaComboBox, false);

        if (nama.isEmpty()) { com.kecamatan.util.UIUtil.setErrorStyle(namaField, true); return; }
        if (selectedDesa == null) { com.kecamatan.util.UIUtil.setErrorStyle(desaComboBox, true); return; }
        if (periodeMulai == null) { com.kecamatan.util.UIUtil.setErrorStyle(periodeMulaiPicker, true); return; }

        String sql;
        if (selectedId == -1) {
            sql = "INSERT INTO kepala_desa (nama, desa_id, periode_mulai, periode_selesai) VALUES (?, ?, ?, ?)";
        } else {
            sql = "UPDATE kepala_desa SET nama = ?, desa_id = ?, periode_mulai = ?, periode_selesai = ? WHERE id = ?";
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            pstmt.setInt(2, selectedDesa.getId());
            pstmt.setObject(3, periodeMulai);
            pstmt.setObject(4, periodeSelesai);
            if (selectedId != -1) pstmt.setInt(5, selectedId);

            pstmt.executeUpdate();
            loadData();
            handleReset();
        } catch (SQLException e) {
            UIUtil.showDatabaseError("Menyimpan Data Kepala Desa", e);
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedId == -1) return;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM kepala_desa WHERE id = ?")) {
            pstmt.setInt(1, selectedId);
            pstmt.executeUpdate();
            loadData();
            handleReset();
        } catch (SQLException e) {
            UIUtil.showDatabaseError("Menghapus Data Kepala Desa", e);
        }
    }

    @FXML
    private void handleReset() {
        selectedId = -1;
        namaField.clear();
        desaComboBox.getSelectionModel().clearSelection();
        periodeMulaiPicker.setValue(null);
        periodeSelesaiPicker.setValue(null);
        kepalaDesaTable.getSelectionModel().clearSelection();

        com.kecamatan.util.UIUtil.setErrorStyle(namaField, false);
        com.kecamatan.util.UIUtil.setErrorStyle(desaComboBox, false);
        com.kecamatan.util.UIUtil.setErrorStyle(periodeMulaiPicker, false);
    }

    @FXML private void goToDashboard() throws IOException { App.setRoot("dashboard", 1200, 800, true); }
    @FXML private void goToDesa() throws IOException { App.setRoot("desa", 1200, 800, true); }
    @FXML private void goToWarga() throws IOException { App.setRoot("warga", 1200, 800, true); }
    @FXML private void goToLaporan() throws IOException { App.setRoot("laporan", 1200, 800, true); }
    @FXML private void goToProfil() throws IOException { App.setRoot("profil", 1200, 800, true); }
    @FXML private void handleLogout() throws IOException {
        com.kecamatan.util.UserSession.logout();
        App.setRoot("login", 800, 450);
    }
}
