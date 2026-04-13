package com.kecamatan.controller;

import com.kecamatan.App;
import com.kecamatan.model.Desa;
import com.kecamatan.model.Kecamatan;
import com.kecamatan.util.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.util.StringConverter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

public class DesaController implements Initializable, DataRefreshable {

    @Override
    public void refreshData() {
        loadSiwalanpanjiId();
        loadDesa();
    }

    @FXML private TableView<Desa> desaTable;
    @FXML private TableColumn<Desa, String> colKecamatan;
    @FXML private TableColumn<Desa, String> colNama;
    @FXML private TableColumn<Desa, String> colKepalaDesa;
    @FXML private TableColumn<Desa, String> colKodePos;
    @FXML private TableColumn<Desa, String> colAlamat;
    @FXML private TableColumn<Desa, Integer> colRT;
    @FXML private TableColumn<Desa, Integer> colRW;

    @FXML private TextField namaField;
    @FXML private TextField kecamatanField;
    @FXML private TextField kepalaDesaField;
    @FXML private TextField kodePosField;
    @FXML private TextArea alamatArea;
    @FXML private VBox rwContainer;

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

    @FXML private TextField searchField;

    private ObservableList<Desa> desaList = FXCollections.observableArrayList();
    private int selectedId = -1;
    private int siwalanpanjoId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        applyRBAC();
        colKecamatan.setCellValueFactory(cellData -> cellData.getValue().kecamatanNamaProperty());
        colNama.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        colKepalaDesa.setCellValueFactory(cellData -> cellData.getValue().kepalaDesaNamaProperty());
        colKodePos.setCellValueFactory(cellData -> cellData.getValue().kodePosProperty());
        colAlamat.setCellValueFactory(cellData -> cellData.getValue().alamatProperty());
        colRT.setCellValueFactory(cellData -> cellData.getValue().jumlahRtProperty().asObject());
        colRW.setCellValueFactory(cellData -> cellData.getValue().jumlahRwProperty().asObject());

        loadSiwalanpanjiId();
        loadDesa();

        desaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedId = newSel.getId();
                namaField.setText(newSel.getNama());
                kepalaDesaField.setText(newSel.getKepalaDesaNama());
                kodePosField.setText(newSel.getKodePos());
                alamatArea.setText(newSel.getAlamat());
                loadRTRWDetails(selectedId);
            }
        });

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            loadDesa();
        });

        // Kode Pos input filter: digits only, max 5
        kodePosField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d{0,5}")) {
                kodePosField.setText(oldVal);
                kodePosField.positionCaret(Math.max(0, kodePosField.getCaretPosition() - 1));
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
            btnDesa, btnKepalaDesa, btnWarga, btnLaporan, btnDashboard);
    }

    private void addRWRow(String rwNo, List<String> rtList) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("rw-row");

        Label rwLabel = new Label("RW:");
        rwLabel.getStyleClass().add("rw-label");
        rwLabel.setMinWidth(30);

        TextField rwField = new TextField(rwNo);
        rwField.setPromptText("...");
        rwField.setPrefWidth(50);
        rwField.setMinWidth(40); // Prevent squishing
        rwField.getStyleClass().add("rw-input");

        FlowPane rtPane = new FlowPane(5, 5);
        HBox.setHgrow(rtPane, Priority.ALWAYS);
        if (rtList != null) {
            for (String rt : rtList) {
                rtPane.getChildren().add(createRTLabel(rt, rtPane));
            }
        }

        Button btnAddRT = new Button("+ RT");
        btnAddRT.getStyleClass().add("rt-add-button");
        btnAddRT.setMinWidth(Control.USE_PREF_SIZE); // Never truncate
        btnAddRT.setOnAction(e -> {
            com.kecamatan.util.UIUtil.showInputDialog("Tambah RT", "Masukkan Nomor RT (Pisahkan dengan koma)", "Nomor RT:")
                .ifPresent(input -> {
                    if (!input.trim().isEmpty()) {
                        String[] parts = input.split(",");
                        for (String part : parts) {
                            String trimmed = part.trim();
                            if (!trimmed.isEmpty()) {
                                // Auto-format numeric RT to 2 digits (e.g., "1" -> "01")
                                if (trimmed.matches("\\d+")) {
                                    try {
                                        int num = Integer.parseInt(trimmed);
                                        trimmed = String.format("%02d", num);
                                    } catch (NumberFormatException ex) {
                                        // Keep as is if parsing fails
                                    }
                                }
                                rtPane.getChildren().add(createRTLabel(trimmed, rtPane));
                            }
                        }
                    }
                });
        });

        Button btnDel = new Button("X");
        btnDel.getStyleClass().add("rt-delete-button");
        btnDel.setMinWidth(Control.USE_PREF_SIZE); // Never truncate
        btnDel.setOnAction(e -> rwContainer.getChildren().remove(row));

        row.getChildren().addAll(rwLabel, rwField, rtPane, btnAddRT, btnDel);
        rwContainer.getChildren().add(row);
    }

    private Label createRTLabel(String text, FlowPane parent) {
        Label label = new Label(text);
        label.getStyleClass().add("rt-tag");
        label.setTooltip(new Tooltip("Klik 2x untuk menghapus"));
        label.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) parent.getChildren().remove(label);
        });
        return label;
    }

    private void loadRTRWDetails(int desaId) {
        rwContainer.getChildren().clear();
        com.kecamatan.util.ThreadManager.execute(() -> {
            String sql = "SELECT rw_number, rt_number FROM desa_rtrw WHERE desa_id = ? ORDER BY rw_number, rt_number";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, desaId);
                ResultSet rs = pstmt.executeQuery();
                
                String currentRW = "";
                List<String> currentRTs = new ArrayList<>();
                List<RWData> rwDataList = new ArrayList<>();

                while (rs.next()) {
                    String rw = rs.getString("rw_number");
                    String rt = rs.getString("rt_number");
                    if (!rw.equals(currentRW)) {
                        if (!currentRW.isEmpty()) {
                            rwDataList.add(new RWData(currentRW, new ArrayList<>(currentRTs)));
                        }
                        currentRW = rw;
                        currentRTs.clear();
                    }
                    currentRTs.add(rt);
                }
                if (!currentRW.isEmpty()) {
                    rwDataList.add(new RWData(currentRW, currentRTs));
                }

                javafx.application.Platform.runLater(() -> {
                    for (RWData rwd : rwDataList) {
                        addRWRow(rwd.rw, rwd.rtList);
                    }
                });
            } catch (SQLException e) {
                UIUtil.showDatabaseError("Memuat RT/RW Desa", e);
            }
        });
    }

    private static class RWData {
        String rw;
        List<String> rtList;
        RWData(String rw, List<String> rtList) { this.rw = rw; this.rtList = rtList; }
    }

    @FXML
    private void handleTambahRW() {
        addRWRow("", new ArrayList<>());
    }

    private void loadSiwalanpanjiId() {
        com.kecamatan.util.ThreadManager.execute(() -> {
            String sql = "SELECT id FROM kecamatan WHERE nama = 'Siwalanpanji'";
            try (Connection conn = DatabaseUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    siwalanpanjoId = rs.getInt("id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadDesa() {
        com.kecamatan.util.ThreadManager.execute(() -> {
            ObservableList<Desa> tempDesa = FXCollections.observableArrayList();
            String searchQuery = searchField != null ? searchField.getText() : null;
            boolean hasSearch = searchQuery != null && !searchQuery.trim().isEmpty();

            StringBuilder sql = new StringBuilder(
                "SELECT d.id, d.kecamatan_id, d.nama, d.kode_pos, d.alamat, d.jumlah_rt, d.jumlah_rw, k.nama as kecamatan_nama, " +
                "(SELECT kd.nama FROM kepala_desa kd WHERE kd.desa_id = d.id AND kd.periode_selesai >= CURRENT_DATE ORDER BY kd.periode_selesai DESC LIMIT 1) as kepala_desa_nama " +
                "FROM desa d JOIN kecamatan k ON d.kecamatan_id = k.id WHERE d.kecamatan_id = ?"
            );
            List<Object> params = new ArrayList<>();
            params.add(siwalanpanjoId);

            if (hasSearch) {
                sql.append(" AND LOWER(d.nama) LIKE LOWER(?)");
                params.add("%" + searchQuery.trim() + "%");
            }

            sql.append(" ORDER BY d.nama");

            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setObject(i + 1, params.get(i));
                }
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    tempDesa.add(new Desa(
                        rs.getInt("id"),
                        rs.getInt("kecamatan_id"),
                        rs.getString("kecamatan_nama"),
                        rs.getString("nama"),
                        0,
                        rs.getInt("jumlah_rt"),
                        rs.getInt("jumlah_rw"),
                        rs.getString("kepala_desa_nama") != null ? rs.getString("kepala_desa_nama") : "-",
                        rs.getString("kode_pos") != null ? rs.getString("kode_pos") : "",
                        rs.getString("alamat") != null ? rs.getString("alamat") : ""
                    ));
                }
                javafx.application.Platform.runLater(() -> {
                    desaList.setAll(tempDesa);
                    desaTable.setItems(desaList);
                });
            } catch (SQLException e) {
                UIUtil.showDatabaseError("Memuat Data Desa", e);
            }
        });
    }

    @FXML
    private void handleSave() {
        String nama = namaField.getText();
        String kodePos = kodePosField.getText();
        String alamat = alamatArea.getText();

        if (siwalanpanjoId == -1) {
            UIUtil.showAlert("Error", "Kecamatan Siwalanpanji tidak ditemukan!", Alert.AlertType.ERROR);
            return;
        }

        // Reset styles
        com.kecamatan.util.UIUtil.setErrorStyle(namaField, false);

        if (nama.isEmpty()) {
            com.kecamatan.util.UIUtil.setErrorStyle(namaField, true);
            return;
        }

        if (nama.length() > 100) {
            com.kecamatan.util.UIUtil.setErrorStyle(namaField, true);
            return;
        }

        // Validate at least one RW with a value
        boolean hasValidRW = false;
        List<RWData> collectedData = new ArrayList<>();
        int totalRTCount = 0;
        java.util.Set<String> uniqueRWs = new java.util.HashSet<>();

        for (javafx.scene.Node node : rwContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                TextField rwF = (TextField) row.getChildren().get(1);
                FlowPane rtP = (FlowPane) row.getChildren().get(2);
                
                String rwVal = rwF.getText().trim();
                if (!rwVal.isEmpty()) {
                    List<String> rts = new ArrayList<>();
                    for (javafx.scene.Node rtNode : rtP.getChildren()) {
                        if (rtNode instanceof Label) {
                            String rtVal = ((Label) rtNode).getText();
                            rts.add(rtVal);
                            totalRTCount++;
                        }
                    }
                    collectedData.add(new RWData(rwVal, rts));
                    uniqueRWs.add(rwVal);
                    hasValidRW = true;
                }
            }
        }

        if (!hasValidRW) {
            UIUtil.showAlert("Error", "Minimal satu RW harus diisi!", Alert.AlertType.ERROR);
            return;
        }

        final int finalRT = totalRTCount;
        final int finalRW = uniqueRWs.size();

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (selectedId == -1) {
                    String sql = "INSERT INTO desa (kecamatan_id, nama, kode_pos, alamat, jumlah_rt, jumlah_rw) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, siwalanpanjoId);
                        pstmt.setString(2, nama);
                        pstmt.setString(3, kodePos);
                        pstmt.setString(4, alamat);
                        pstmt.setInt(5, finalRT);
                        pstmt.setInt(6, finalRW);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) selectedId = rs.getInt(1);
                    }
                } else {
                    String sql = "UPDATE desa SET kecamatan_id = ?, nama = ?, kode_pos = ?, alamat = ?, jumlah_rt = ?, jumlah_rw = ? WHERE id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, siwalanpanjoId);
                        pstmt.setString(2, nama);
                        pstmt.setString(3, kodePos);
                        pstmt.setString(4, alamat);
                        pstmt.setInt(5, finalRT);
                        pstmt.setInt(6, finalRW);
                        pstmt.setInt(7, selectedId);
                        pstmt.executeUpdate();
                    }
                    // Clear old details
                    try (PreparedStatement delStmt = conn.prepareStatement("DELETE FROM desa_rtrw WHERE desa_id = ?")) {
                        delStmt.setInt(1, selectedId);
                        delStmt.executeUpdate();
                    }
                }

                // Insert new details
                String insSql = "INSERT INTO desa_rtrw (desa_id, rw_number, rt_number) VALUES (?, ?, ?)";
                try (PreparedStatement insStmt = conn.prepareStatement(insSql)) {
                    for (RWData rwd : collectedData) {
                        for (String rt : rwd.rtList) {
                            insStmt.setInt(1, selectedId);
                            insStmt.setString(2, rwd.rw);
                            insStmt.setString(3, rt);
                            insStmt.addBatch();
                        }
                    }
                    insStmt.executeBatch();
                }

                conn.commit();
                loadDesa();
                handleReset();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            UIUtil.showDatabaseError("Menyimpan Data Desa", e);
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedId == -1) return;
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "DELETE FROM desa WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, selectedId);
                pstmt.executeUpdate();
            }
            loadDesa();
            handleReset();
        } catch (SQLException e) {
            UIUtil.showDatabaseError("Menghapus Data Desa", e);
        }
    }

    @FXML
    private void handleReset() {
        selectedId = -1;
        namaField.clear();
        kepalaDesaField.clear();
        kodePosField.clear();
        alamatArea.clear();
        rwContainer.getChildren().clear();
        if (desaTable != null) desaTable.getSelectionModel().clearSelection();

        // Clear error styles
        com.kecamatan.util.UIUtil.setErrorStyle(namaField, false);
    }

    @FXML private void goToDashboard() throws IOException { App.setRoot("dashboard", 1200, 800, true); }
    @FXML private void goToKepalaDesa() throws IOException { App.setRoot("kepala_desa", 1200, 800, true); }
    @FXML private void goToWarga() throws IOException { App.setRoot("warga", 1200, 800, true); }
    @FXML private void goToLaporan() throws IOException { App.setRoot("laporan", 1200, 800, true); }
    @FXML private void goToProfil() throws IOException { App.setRoot("profil", 1200, 800, true); }
    @FXML private void handleLogout() throws IOException { 
        com.kecamatan.util.UserSession.logout();
        App.setRoot("login", 800, 450); 
    }
}
