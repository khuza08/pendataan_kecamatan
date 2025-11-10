// src/pendataankecamatan/dialogs/DesaDialog.java
package pendataankecamatan.dialogs;

import pendataankecamatan.controller.AdminController;
import pendataankecamatan.model.Desa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DesaDialog extends JDialog {
    private JTextField fieldNama, fieldKodePos;
    private JButton btnSave, btnCancel;
    private AdminController controller;
    private Desa currentDesa;

    public DesaDialog(Frame parent, boolean modal, Desa desa) {
        super(parent, modal);
        this.currentDesa = desa;
        this.controller = new AdminController();
        setTitle(currentDesa == null ? "Tambah Desa" : "Edit Desa");
        setSize(400, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        populateFields();
    }

    private void initializeComponents() {
        fieldNama = new JTextField(15);
        fieldKodePos = new JTextField(10);

        btnSave = new JButton("Simpan");
        btnCancel = new JButton("Batal");
    }

    private void setupLayout() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nama Desa:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fieldNama, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Kode Pos:"), gbc);
        gbc.gridx = 1;
        formPanel.add(fieldKodePos, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        btnSave.addActionListener(e -> saveDesa());
        btnCancel.addActionListener(e -> dispose());
    }

    private void populateFields() {
        if (currentDesa != null) {
            fieldNama.setText(currentDesa.getNama());
            fieldKodePos.setText(currentDesa.getKodePos());
        }
    }

    private void saveDesa() {
        String nama = fieldNama.getText().trim();
        String kodePos = fieldKodePos.getText().trim();

        if (nama.isEmpty() || kodePos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan Kode Pos harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (currentDesa == null) {
            currentDesa = new Desa(0, nama, kodePos);
        } else {
            currentDesa.setNama(nama);
            currentDesa.setKodePos(kodePos);
        }

        boolean success = controller.updateDesa(currentDesa);
        if (success) {
            JOptionPane.showMessageDialog(this, "Data desa berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data desa.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}