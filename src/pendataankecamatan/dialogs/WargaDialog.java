// src/pendataankecamatan/dialogs/WargaDialog.java
package pendataankecamatan.dialogs;

import pendataankecamatan.controller.AdminController;
import pendataankecamatan.model.Warga;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WargaDialog extends JDialog {
    private JTextField fieldNik, fieldNamaLengkap, fieldAlamat, fieldRt, fieldRw, fieldDesaId, fieldTanggalLahir;
    private JComboBox<String> comboJenisKelamin;
    private JButton btnSave, btnCancel;
    private AdminController controller;
    private Warga currentWarga;

    public WargaDialog(Frame parent, boolean modal, Warga warga) {
        super(parent, modal);
        this.currentWarga = warga;
        this.controller = new AdminController();
        setTitle(currentWarga == null ? "Tambah Warga" : "Edit Warga");
        setSize(500, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        populateFields();
    }

    private void initializeComponents() {
        fieldNik = new JTextField(15);
        fieldNamaLengkap = new JTextField(15);
        fieldAlamat = new JTextField(15);
        fieldRt = new JTextField(10);
        fieldRw = new JTextField(10);
        fieldDesaId = new JTextField(10);
        fieldTanggalLahir = new JTextField(10); // format: yyyy-MM-dd
        comboJenisKelamin = new JComboBox<>(new String[]{"L", "P"});

        btnSave = new JButton("Simpan");
        btnCancel = new JButton("Batal");
    }

    private void setupLayout() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("NIK:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fieldNik, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fieldNamaLengkap, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fieldAlamat, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("RT:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fieldRt, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("RW:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fieldRw, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Desa ID:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fieldDesaId, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Jenis Kelamin:"), gbc);
        gbc.gridx = 1;
        formPanel.add(comboJenisKelamin, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Tanggal Lahir (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(fieldTanggalLahir, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        btnSave.addActionListener(e -> saveWarga());
        btnCancel.addActionListener(e -> dispose());
    }

    private void populateFields() {
        if (currentWarga != null) {
            fieldNik.setText(currentWarga.getNik());
            fieldNamaLengkap.setText(currentWarga.getNamaLengkap());
            fieldAlamat.setText(currentWarga.getAlamat());
            fieldRt.setText(currentWarga.getRt());
            fieldRw.setText(currentWarga.getRw());
            fieldDesaId.setText(String.valueOf(currentWarga.getDesaId()));
            comboJenisKelamin.setSelectedItem(currentWarga.getJenisKelamin());
            fieldTanggalLahir.setText(currentWarga.getTanggalLahir());
        }
    }

    private void saveWarga() {
        String nik = fieldNik.getText().trim();
        String namaLengkap = fieldNamaLengkap.getText().trim();
        String alamat = fieldAlamat.getText().trim();
        String rt = fieldRt.getText().trim();
        String rw = fieldRw.getText().trim();
        String desaIdText = fieldDesaId.getText().trim();
        String jenisKelamin = (String) comboJenisKelamin.getSelectedItem();
        String tanggalLahir = fieldTanggalLahir.getText().trim();

        if (nik.isEmpty() || namaLengkap.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIK, Nama Lengkap, dan Alamat harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int desaId = 1; // default
        try {
            desaId = Integer.parseInt(desaIdText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Desa ID harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!tanggalLahir.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Format tanggal lahir harus yyyy-MM-dd!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (currentWarga == null) {
            currentWarga = new Warga(0, 0, nik, namaLengkap, alamat, rt, rw, desaId, jenisKelamin, tanggalLahir);
        } else {
            currentWarga.setNik(nik);
            currentWarga.setNamaLengkap(namaLengkap);
            currentWarga.setAlamat(alamat);
            currentWarga.setRt(rt);
            currentWarga.setRw(rw);
            currentWarga.setDesaId(desaId);
            currentWarga.setJenisKelamin(jenisKelamin);
            currentWarga.setTanggalLahir(tanggalLahir);
        }

        boolean success = controller.updateWarga(currentWarga);
        if (success) {
            JOptionPane.showMessageDialog(this, "Data warga berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data warga.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}