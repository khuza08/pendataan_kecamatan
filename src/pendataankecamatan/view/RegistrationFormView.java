// src/pendataankecamatan/view/RegistrationFormView.java
package pendataankecamatan.view;

import com.formdev.flatlaf.FlatLightLaf;
import pendataankecamatan.controller.RegistrationController;
import pendataankecamatan.model.Warga;
import pendataankecamatan.model.User;
import pendataankecamatan.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationFormView extends JFrame {
    private JTextField fieldNik;
    private JTextField fieldNama;
    private JTextField fieldAlamat;
    private JTextField fieldRt;
    private JTextField fieldRw;
    private JComboBox<String> comboJenisKelamin;
    private JComboBox<String> comboDesa;
    private JTextField fieldTanggalLahir; // 🔹 GANTI: Pakai JTextField biasa
    private JButton buttonSimpan;
    private JButton buttonBatal;

    public RegistrationFormView() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 15);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setUndecorated(true);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadExistingData();
    }

    private void initializeComponents() {
        fieldNik = new JTextField(15);
        fieldNama = new JTextField(15);
        fieldAlamat = new JTextField(15);
        fieldRt = new JTextField(5);
        fieldRw = new JTextField(5);
        comboJenisKelamin = new JComboBox<>(new String[]{"L", "P"});
        comboDesa = new JComboBox<>(new String[]{"Siwalan Panji", "Balonggarut", "Balong Dowo", "Balong Rejo", "Balong Tengah"});

        // 🔹 GANTI: Pakai JTextField biasa
        fieldTanggalLahir = new JTextField("2004-01-01", 10);

        buttonSimpan = new JButton("Simpan");
        buttonBatal = new JButton("Batal");

        buttonSimpan.setBackground(new Color(0x006315));
        buttonSimpan.setForeground(Color.WHITE);
        buttonSimpan.setFocusPainted(false);

        buttonBatal.setBackground(new Color(180, 180, 180));
        buttonBatal.setForeground(Color.WHITE);
        buttonBatal.setFocusPainted(false);
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("Formulir Pendataan Warga");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        mainPanel.add(title, gbc);

        // NIK
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        mainPanel.add(new JLabel("NIK:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        mainPanel.add(fieldNik, gbc);

        // Nama Lengkap
        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Nama Lengkap:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        mainPanel.add(fieldNama, gbc);

        // Alamat
        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        mainPanel.add(fieldAlamat, gbc);

        // RT
        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("RT:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        mainPanel.add(fieldRt, gbc);

        // RW
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("RW:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        mainPanel.add(fieldRw, gbc);

        // Jenis Kelamin
        gbc.gridx = 0; gbc.gridy = 6;
        mainPanel.add(new JLabel("Jenis Kelamin:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        mainPanel.add(comboJenisKelamin, gbc);

        // Desa
        gbc.gridx = 0; gbc.gridy = 7;
        mainPanel.add(new JLabel("Desa:"), gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        mainPanel.add(comboDesa, gbc);

        // Tanggal Lahir
        gbc.gridx = 0; gbc.gridy = 8;
        mainPanel.add(new JLabel("Tanggal Lahir:"), gbc);
        gbc.gridx = 1; gbc.gridy = 8;
        mainPanel.add(fieldTanggalLahir, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.add(buttonSimpan);
        buttonPanel.add(buttonBatal);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        buttonSimpan.addActionListener(e -> handleSimpan());
        buttonBatal.addActionListener(e -> dispose());
    }

    private void loadExistingData() {
        User currentUser = Constants.CURRENT_USER;
        if (currentUser != null) {
            RegistrationController controller = new RegistrationController();
            Warga warga = controller.getProfile(currentUser.getId());

            if (warga != null) {
                fieldNik.setText(warga.getNik());
                fieldNama.setText(warga.getNamaLengkap());
                fieldAlamat.setText(warga.getAlamat());
                fieldRt.setText(warga.getRt());
                fieldRw.setText(warga.getRw());
                comboJenisKelamin.setSelectedItem(warga.getJenisKelamin());
                comboDesa.setSelectedItem("Siwalan Panji"); // sesuaikan dengan data sebenarnya
                fieldTanggalLahir.setText(warga.getTanggalLahir()); // 🔹 SEKARANG BISA
            }
        }
    }

    private void handleSimpan() {
        String nik = fieldNik.getText().trim();
        String nama = fieldNama.getText().trim();
        String alamat = fieldAlamat.getText().trim();
        String rt = fieldRt.getText().trim();
        String rw = fieldRw.getText().trim();
        String jenisKelamin = (String) comboJenisKelamin.getSelectedItem();
        String tanggalLahir = fieldTanggalLahir.getText().trim();

        // 🔹 Validasi format tanggal (opsional)
        if (!tanggalLahir.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Format tanggal lahir harus yyyy-MM-dd!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nik.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIK, Nama Lengkap, dan Alamat harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User currentUser = Constants.CURRENT_USER;
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Anda harus login terlebih dahulu!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Simpan ke database
        RegistrationController controller = new RegistrationController();
        Warga warga = new Warga(0, currentUser.getId(), nik, nama, alamat, rt, rw, 1, jenisKelamin, tanggalLahir);

        if (controller.updateProfile(warga)) {
            JOptionPane.showMessageDialog(this, "Data warga berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data warga!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationFormView().setVisible(true));
    }
}