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

import com.kecamatan.util.DataRefreshable;

public class DesaController implements Initializable, DataRefreshable {

    @Override
    public void refreshData() {
        loadKecamatan();
        loadDesa();
    }

    @FXML private TableView<Desa> desaTable;
    @FXML private TableColumn<Desa, String> colKecamatan;
    @FXML private TableColumn<Desa, String> colNama;
    @FXML private TableColumn<Desa, Integer> colPopulasi;
    @FXML private TableColumn<Desa, Integer> colRT;
    @FXML private TableColumn<Desa, Integer> colRW;

    @FXML private ComboBox<Kecamatan> kecamatanComboBox;
    @FXML private TextField namaField;
    @FXML private VBox rwContainer;

    private ObservableList<Desa> desaList = FXCollections.observableArrayList();
    private ObservableList<Kecamatan> kecamatanList = FXCollections.observableArrayList();
    private int selectedId = -1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colKecamatan.setCellValueFactory(cellData -> cellData.getValue().kecamatanNamaProperty());
        colNama.setCellValueFactory(cellData -> cellData.getValue().namaProperty());
        colPopulasi.setCellValueFactory(cellData -> cellData.getValue().populasiProperty().asObject());
        colRT.setCellValueFactory(cellData -> cellData.getValue().jumlahRtProperty().asObject());
        colRW.setCellValueFactory(cellData -> cellData.getValue().jumlahRwProperty().asObject());

        setupComboBox();
        loadKecamatan();
        loadDesa();

        desaTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedId = newSel.getId();
                namaField.setText(newSel.getNama());
                
                for (Kecamatan k : kecamatanList) {
                    if (k.getId() == newSel.getKecamatanId()) {
                        kecamatanComboBox.getSelectionModel().select(k);
                        break;
                    }
                }

                loadRTRWDetails(selectedId);
            }
        });
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
                e.printStackTrace();
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

    private void setupComboBox() {
        kecamatanComboBox.setConverter(new StringConverter<Kecamatan>() {
            @Override
            public String toString(Kecamatan k) {
                return k == null ? "" : k.getNama();
            }

            @Override
            public Kecamatan fromString(String string) {
                return null;
            }
        });
    }

    private void loadKecamatan() {
        com.kecamatan.util.ThreadManager.execute(() -> {
            ObservableList<Kecamatan> tempKec = FXCollections.observableArrayList();
            String sql = "SELECT k.*, " +
                         "(SELECT COUNT(*) FROM desa WHERE kecamatan_id = k.id) as calculated_desa_count, " +
                         "(SELECT COUNT(*) FROM warga w JOIN desa d ON w.desa_id = d.id WHERE d.kecamatan_id = k.id) as calculated_pop " +
                         "FROM kecamatan k ORDER BY k.nama";
            try (Connection conn = DatabaseUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    tempKec.add(new Kecamatan(
                        rs.getInt("id"),
                        rs.getString("kode"),
                        rs.getString("nama"),
                        rs.getInt("calculated_desa_count"),
                        rs.getInt("calculated_pop")
                    ));
                }
                javafx.application.Platform.runLater(() -> {
                    kecamatanList.setAll(tempKec);
                    kecamatanComboBox.setItems(kecamatanList);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void loadDesa() {
        com.kecamatan.util.ThreadManager.execute(() -> {
            ObservableList<Desa> tempDesa = FXCollections.observableArrayList();
            String sql = "SELECT d.*, k.nama as kecamatan_nama, (SELECT COUNT(*) FROM warga WHERE desa_id = d.id) as calculated_pop FROM desa d JOIN kecamatan k ON d.kecamatan_id = k.id ORDER BY d.nama";
            try (Connection conn = DatabaseUtil.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    tempDesa.add(new Desa(
                        rs.getInt("id"),
                        rs.getInt("kecamatan_id"),
                        rs.getString("kecamatan_nama"),
                        rs.getString("nama"),
                        rs.getInt("calculated_pop"),
                        rs.getInt("jumlah_rt"),
                        rs.getInt("jumlah_rw")
                    ));
                }
                javafx.application.Platform.runLater(() -> {
                    desaList.setAll(tempDesa);
                    desaTable.setItems(desaList);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleSave() {
        Kecamatan selectedKec = kecamatanComboBox.getSelectionModel().getSelectedItem();
        String nama = namaField.getText();

        // Reset styles
        com.kecamatan.util.UIUtil.setErrorStyle(kecamatanComboBox, false);
        com.kecamatan.util.UIUtil.setErrorStyle(namaField, false);

        boolean hasError = false;
        if (selectedKec == null) { com.kecamatan.util.UIUtil.setErrorStyle(kecamatanComboBox, true); hasError = true; }
        if (nama.isEmpty()) { com.kecamatan.util.UIUtil.setErrorStyle(namaField, true); hasError = true; }

        if (hasError) return;

        // Collect dynamic data
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
                }
            }
        }

        final int finalRT = totalRTCount;
        final int finalRW = uniqueRWs.size();

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (selectedId == -1) {
                    String sql = "INSERT INTO desa (kecamatan_id, nama, jumlah_rt, jumlah_rw) VALUES (?, ?, ?, ?) RETURNING id";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, selectedKec.getId());
                        pstmt.setString(2, nama);
                        pstmt.setInt(3, finalRT);
                        pstmt.setInt(4, finalRW);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next()) selectedId = rs.getInt(1);
                    }
                } else {
                    String sql = "UPDATE desa SET kecamatan_id = ?, nama = ?, jumlah_rt = ?, jumlah_rw = ? WHERE id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, selectedKec.getId());
                        pstmt.setString(2, nama);
                        pstmt.setInt(3, finalRT);
                        pstmt.setInt(4, finalRW);
                        pstmt.setInt(5, selectedId);
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReset() {
        selectedId = -1;
        kecamatanComboBox.getSelectionModel().clearSelection();
        namaField.clear();
        rwContainer.getChildren().clear();
        if (desaTable != null) desaTable.getSelectionModel().clearSelection();

        // Clear error styles
        com.kecamatan.util.UIUtil.setErrorStyle(kecamatanComboBox, false);
        com.kecamatan.util.UIUtil.setErrorStyle(namaField, false);
    }

    @FXML private void goToDashboard() throws IOException { App.setRoot("dashboard", 1200, 800, true); }
    @FXML private void goToKecamatan() throws IOException { App.setRoot("kecamatan", 1200, 800, true); }
    @FXML private void goToWarga() throws IOException { App.setRoot("warga", 1200, 800, true); }
    @FXML private void goToLaporan() throws IOException { App.setRoot("laporan", 1200, 800, true); }
    @FXML private void handleLogout() throws IOException { App.setRoot("login", 800, 450); }
}
