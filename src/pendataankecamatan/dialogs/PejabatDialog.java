// src/pendataankecamatan/dialogs/PejabatDialog.java
package pendataankecamatan.dialogs;

import pendataankecamatan.controller.AdminController;
import pendataankecamatan.model.Pejabat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PejabatDialog extends JDialog {
    private JTextField fieldNama, fieldJabatan, fieldNomorTelepon;
    private JButton btnSave, btnCancel;
    private AdminController controller;
    private Pejabat currentPejabat;

    public PejabatDialog(Frame parent, boolean modal, Pejabat pejabat) {
        super(parent, modal);
        this.currentPejabat = pejabat;
        this.controller = new AdminController();
        setTitle(currentPejabat == null ? "Tambah Pejabat" : "Edit Pejabat");
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        populateFields();
    }

    private void initializeComponents() {
        fieldNama = new JTextField(15);
        fieldJabatan = new JTextField(15);
        fieldNomorTelepon = new JTextField(15);

        btnSave = new JButton("Simpan");
        btnCancel = new JButton("Batal");
    }

    private void setupLayout() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fieldNama, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Jabatan:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fieldJabatan, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Nomor Telepon:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fieldNomorTelepon, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        btnSave.addActionListener(e -> savePejabat());
        btnCancel.addActionListener(e -> dispose());
    }

    private void populateFields() {
        if (currentPejabat != null) {
            fieldNama.setText(currentPejabat.getNama());
            fieldJabatan.setText(currentPejabat.getJabatan());
            fieldNomorTelepon.setText(currentPejabat.getNomorTelepon());
        }
    }

    private void savePejabat() {
        String nama = fieldNama.getText().trim();
        String jabatan = fieldJabatan.getText().trim();
        String nomorTelepon = fieldNomorTelepon.getText().trim();

        if (nama.isEmpty() || jabatan.isEmpty() || nomorTelepon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama, Jabatan, dan Nomor Telepon harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (currentPejabat == null) {
            currentPejabat = new Pejabat(0, nama, jabatan, nomorTelepon);
        } else {
            currentPejabat.setNama(nama);
            currentPejabat.setJabatan(jabatan);
            currentPejabat.setNomorTelepon(nomorTelepon);
        }

        boolean success = controller.updatePejabat(currentPejabat);
        if (success) {
            JOptionPane.showMessageDialog(this, "Data pejabat berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data pejabat.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}