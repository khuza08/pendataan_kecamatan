# Data Desa - Alamat & Kode Pos Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add Alamat (textarea) and Kode Pos (5-digit) fields to Data Desa page, replace Populasi column with Kode Pos + Alamat columns in table.

**Architecture:** Add 2 DB columns to `desa` table, extend `Desa.java` model with new properties, update FXML table/form, update `DesaController.java` load/save/reset logic.

**Tech Stack:** JavaFX 21, PostgreSQL, HikariCP

---

### Task 1: Database Migration — Add kode_pos and alamat columns

**Files:**
- Modify: Database via `psql` command

- [ ] **Step 1: Add columns to desa table**

Run:
```bash
psql -U huza -d pendataankecamatan << 'EOF'
ALTER TABLE desa ADD COLUMN IF NOT EXISTS kode_pos VARCHAR(5);
ALTER TABLE desa ADD COLUMN IF NOT EXISTS alamat TEXT;

-- Verify
SELECT column_name, data_type FROM information_schema.columns 
WHERE table_name = 'desa' ORDER BY ordinal_position;
EOF
```
Expected: `kode_pos` (character varying) and `alamat` (text) appear in output.

- [ ] **Step 2: Update test data with realistic values**

Run:
```bash
psql -U huza -d pendataankecamatan << 'EOF'
UPDATE desa SET kode_pos = '61251', alamat = 'Jl. Raya Siwalanpanjo No. 1' WHERE nama = 'Siwalanpanjo';
UPDATE desa SET kode_pos = '61252', alamat = 'Jl. Dahlia No. 15, RT 01/RW 01' WHERE nama = 'Bulusidokare';
UPDATE desa SET kode_pos = '61253', alamat = 'Jl. Mawar No. 5' WHERE nama = 'Gelam';
UPDATE desa SET kode_pos = '61254', alamat = 'Jl. Sedap Malam No. 11' WHERE nama = 'Jabon';
UPDATE desa SET kode_pos = '61255', alamat = 'Jl. Bakung No. 2, RT 02/RW 01' WHERE nama = 'Pepelegi';

SELECT nama, kode_pos, alamat FROM desa ORDER BY nama;
EOF
```

- [ ] **Step 3: Commit**

```bash
git add .
git commit -m "feat(desa): add kode_pos and alamat columns to desa table"
```

---

### Task 2: Model Update — Add kodePos and alamat properties to Desa.java

**Files:**
- Modify: `src/main/java/com/kecamatan/model/Desa.java`

- [ ] **Step 1: Add properties**

Add to `Desa.java`:
```java
// After existing properties
private final SimpleStringProperty kodePos;
private final SimpleStringProperty alamat;

// Update constructor to accept new params
public Desa(int id, int kecamatanId, String kecamatanNama, String nama, int populasi, int jumlahRt, int jumlahRw) {
    this(id, kecamatanId, kecamatanNama, nama, populasi, jumlahRt, jumlahRw, "", "", "");
}

public Desa(int id, int kecamatanId, String kecamatanNama, String nama, int populasi, int jumlahRt, int jumlahRw, String kepalaDesaNama) {
    this(id, kecamatanId, kecamatanNama, nama, populasi, jumlahRt, jumlahRw, kepalaDesaNama, "", "");
}

public Desa(int id, int kecamatanId, String kecamatanNama, String nama, int populasi, int jumlahRt, int jumlahRw, String kepalaDesaNama, String kodePos, String alamat) {
    this.id = new SimpleIntegerProperty(id);
    this.kecamatanId = new SimpleIntegerProperty(kecamatanId);
    this.kecamatanNama = new SimpleStringProperty(kecamatanNama);
    this.nama = new SimpleStringProperty(nama);
    this.populasi = new SimpleIntegerProperty(populasi);
    this.jumlahRt = new SimpleIntegerProperty(jumlahRt);
    this.jumlahRw = new SimpleIntegerProperty(jumlahRw);
    this.kepalaDesaNama = new SimpleStringProperty(kepalaDesaNama);
    this.kodePos = new SimpleStringProperty(kodePos);
    this.alamat = new SimpleStringProperty(alamat);
}

// Add getters/properties
public String getKodePos() { return kodePos.get(); }
public SimpleStringProperty kodePosProperty() { return kodePos; }

public String getAlamat() { return alamat.get(); }
public SimpleStringProperty alamatProperty() { return alamat; }
```

- [ ] **Step 2: Verify compilation**

Run: `mvn compile`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/kecamatan/model/Desa.java
git commit -m "feat(desa): add kodePos and alamat properties to Desa model"
```

---

### Task 3: FXML Update — Table columns + form fields

**Files:**
- Modify: `src/main/resources/com/kecamatan/view/desa.fxml`

- [ ] **Step 1: Update table columns**

Replace the table columns section:
```xml
<columns>
    <TableColumn fx:id="colKecamatan" text="Kecamatan" prefWidth="120"/>
    <TableColumn fx:id="colNama" text="Nama Desa" prefWidth="130"/>
    <TableColumn fx:id="colKepalaDesa" text="Kepala Desa" prefWidth="160"/>
    <TableColumn fx:id="colKodePos" text="Kode Pos" prefWidth="80"/>
    <TableColumn fx:id="colAlamat" text="Alamat" prefWidth="200"/>
    <TableColumn fx:id="colRT" text="RT" prefWidth="60"/>
    <TableColumn fx:id="colRW" text="RW" prefWidth="60"/>
</columns>
```

- [ ] **Step 2: Add form fields**

After the Kepala Desa field, add:
```xml
<VBox spacing="5">
    <Label text="Kode Pos" styleClass="input-label"/>
    <TextField fx:id="kodePosField" promptText="61xxx" styleClass="input-field" maxLength="5"/>
</VBox>

<VBox spacing="5">
    <Label text="Alamat" styleClass="input-label"/>
    <TextArea fx:id="alamatArea" promptText="Jl. ..., RT ..., RW ..." styleClass="input-field" wrapText="true"/>
</VBox>
```

- [ ] **Step 3: Verify compilation**

Run: `mvn compile`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/com/kecamatan/view/desa.fxml
git commit -m "feat(desa): add Kode Pos and Alamat columns and form fields"
```

---

### Task 4: Controller Update — Bindings, load, save, reset

**Files:**
- Modify: `src/main/java/com/kecamatan/controller/DesaController.java`

- [ ] **Step 1: Add @FXML fields**

Add after `colRW`:
```java
@FXML private TableColumn<Desa, String> colKodePos;
@FXML private TableColumn<Desa, String> colAlamat;
```

Add after `kepalaDesaField`:
```java
@FXML private TextField kodePosField;
@FXML private TextArea alamatArea;
```

- [ ] **Step 2: Add column bindings in initialize()**

After `colRW.setCellValueFactory(...)`, add:
```java
colKodePos.setCellValueFactory(cellData -> cellData.getValue().kodePosProperty());
colAlamat.setCellValueFactory(cellData -> cellData.getValue().alamatProperty());
```

- [ ] **Step 3: Add Kode Pos input filter**

Add before `startClock()`:
```java
// Kode Pos input filter: digits only, max 5
kodePosField.textProperty().addListener((obs, oldVal, newVal) -> {
    if (newVal != null && !newVal.matches("\\d{0,5}")) {
        kodePosField.setText(oldVal);
        kodePosField.positionCaret(kodePosField.getCaretPosition() - 1);
    }
});
```

- [ ] **Step 4: Update table selection listener**

Update the listener to include new fields:
```java
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
```

- [ ] **Step 5: Update loadDesa() SQL**

Update the SELECT to include new columns:
```java
StringBuilder sql = new StringBuilder(
    "SELECT d.id, d.kecamatan_id, d.nama, d.kode_pos, d.alamat, d.jumlah_rt, d.jumlah_rw, k.nama as kecamatan_nama, " +
    "(SELECT kd.nama FROM kepala_desa kd WHERE kd.desa_id = d.id AND kd.periode_selesai >= CURRENT_DATE ORDER BY kd.periode_selesai DESC LIMIT 1) as kepala_desa_nama " +
    "FROM desa d JOIN kecamatan k ON d.kecamatan_id = k.id WHERE d.kecamatan_id = ?"
);
```

Update the `while (rs.next())` block:
```java
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
```

- [ ] **Step 6: Update handleSave() SQL**

Add field reads at start:
```java
String kodePos = kodePosField.getText();
String alamat = alamatArea.getText();
```

Update INSERT:
```java
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
}
```

Update UPDATE:
```java
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
    // ... rest unchanged (clear old details, insert new, commit)
}
```

- [ ] **Step 7: Update handleReset()**

Add:
```java
kodePosField.clear();
alamatArea.clear();
```

- [ ] **Step 8: Compile and verify**

Run: `mvn compile`
Expected: BUILD SUCCESS

- [ ] **Step 9: Commit**

```bash
git add src/main/java/com/kecamatan/controller/DesaController.java
git commit -m "feat(desa): implement Alamat & Kode Pos in controller (load, save, reset, validation)"
```

---

### Task 5: Final verification and run

- [ ] **Step 1: Full compilation**

Run: `mvn compile`
Expected: BUILD SUCCESS

- [ ] **Step 2: Run the application**

Run: `mvn javafx:run`
Expected: App launches, Data Desa page shows Kode Pos + Alamat columns, form has new fields, existing test data displays correctly.

- [ ] **Step 3: Final commit**

```bash
git add .
git commit -m "feat(desa): complete Alamat & Kode Pos feature"
```
