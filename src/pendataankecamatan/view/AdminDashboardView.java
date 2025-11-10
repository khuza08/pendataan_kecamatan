// src/pendataankecamatan/view/AdminDashboardView.java
package pendataankecamatan.view;

import com.formdev.flatlaf.FlatLightLaf;
import pendataankecamatan.controller.AdminController;
import pendataankecamatan.model.Desa;
import pendataankecamatan.model.Warga;
import pendataankecamatan.model.Pejabat;
import pendataankecamatan.util.Constants;
import pendataankecamatan.dialogs.DesaDialog;
import pendataankecamatan.dialogs.WargaDialog;
import pendataankecamatan.dialogs.PejabatDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdminDashboardView extends JFrame {
    private JTabbedPane tabbedPane;
    private JPanel panelDesa, panelWarga, panelPejabat;
    private JTable tableDesa, tableWarga, tablePejabat;
    private DefaultTableModel modelDesa, modelWarga, modelPejabat;
    private AdminController controller;

    public AdminDashboardView() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Admin Dashboard - Kecamatan Siwalan Panji");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        controller = new AdminController();

        initializeUI();
        loadAllData();
    }

    private void initializeUI() {
        tabbedPane = new JTabbedPane();

        // Tab Desa
        panelDesa = createCrudPanel("Desa", "desa");
        tabbedPane.addTab("Kelola Desa", panelDesa);

        // Tab Warga
        panelWarga = createCrudPanel("Warga", "warga");
        tabbedPane.addTab("Kelola Warga", panelWarga);

        // Tab Pejabat
        panelPejabat = createCrudPanel("Pejabat", "pejabat");
        tabbedPane.addTab("Kelola Pejabat", panelPejabat);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCrudPanel(String entity, String type) {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdd = new JButton("Tambah");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Hapus");

        btnAdd.addActionListener(e -> openAddDialog(type));
        btnEdit.addActionListener(e -> openEditDialog(type));
        btnDelete.addActionListener(e -> deleteSelected(type));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        // Table
        String[] columns = getColumnsForEntity(type);
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Assign model to corresponding variable
        if ("desa".equals(type)) {
            modelDesa = model;
            tableDesa = table;
        } else if ("warga".equals(type)) {
            modelWarga = model;
            tableWarga = table;
        } else if ("pejabat".equals(type)) {
            modelPejabat = model;
            tablePejabat = table;
        }

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private String[] getColumnsForEntity(String type) {
        switch (type) {
            case "desa":
                return new String[]{"ID", "Nama", "Kode Pos"};
            case "warga":
                return new String[]{"ID", "User ID", "NIK", "Nama Lengkap", "Alamat", "RT", "RW", "Desa ID", "Jenis Kelamin", "Tanggal Lahir"};
            case "pejabat":
                return new String[]{"ID", "Nama", "Jabatan", "No Telepon"};
            default:
                return new String[]{};
        }
    }

    private void loadAllData() {
        loadDesaData();
        loadWargaData();
        loadPejabatData();
    }

    private void loadDesaData() {
        modelDesa.setRowCount(0); // Clear existing rows
        List<Desa> list = controller.getAllDesa();
        for (Desa item : list) {
            modelDesa.addRow(new Object[]{
                item.getId(),
                item.getNama(),
                item.getKodePos()
            });
        }
    }

    private void loadWargaData() {
        modelWarga.setRowCount(0);
        List<Warga> list = controller.getAllWarga();
        for (Warga item : list) {
            modelWarga.addRow(new Object[]{
                item.getId(),
                item.getUserId(),
                item.getNik(),
                item.getNamaLengkap(),
                item.getAlamat(),
                item.getRt(),
                item.getRw(),
                item.getDesaId(),
                item.getJenisKelamin(),
                item.getTanggalLahir()
            });
        }
    }

    private void loadPejabatData() {
        modelPejabat.setRowCount(0);
        List<Pejabat> list = controller.getAllPejabat();
        for (Pejabat item : list) {
            modelPejabat.addRow(new Object[]{
                item.getId(),
                item.getNama(),
                item.getJabatan(),
                item.getNomorTelepon()
            });
        }
    }

    private void openAddDialog(String type) {
        switch (type) {
            case "desa":
                new DesaDialog(this, true, null).setVisible(true);
                break;
            case "warga":
                new WargaDialog(this, true, null).setVisible(true);
                break;
            case "pejabat":
                new PejabatDialog(this, true, null).setVisible(true);
                break;
        }
        refreshData(type);
    }

    private void openEditDialog(String type) {
        int selectedRow = -1;
        if ("desa".equals(type)) selectedRow = tableDesa.getSelectedRow();
        else if ("warga".equals(type)) selectedRow = tableWarga.getSelectedRow();
        else if ("pejabat".equals(type)) selectedRow = tablePejabat.getSelectedRow();

        if (selectedRow >= 0) {
            switch (type) {
                case "desa":
                    int id = (int) modelDesa.getValueAt(selectedRow, 0);
                    Desa desa = controller.getDesaById(id);
                    new DesaDialog(this, true, desa).setVisible(true);
                    break;
                case "warga":
                    int wid = (int) modelWarga.getValueAt(selectedRow, 0);
                    Warga warga = controller.getWargaById(wid);
                    new WargaDialog(this, true, warga).setVisible(true);
                    break;
                case "pejabat":
                    int pid = (int) modelPejabat.getValueAt(selectedRow, 0);
                    Pejabat pejabat = controller.getPejabatById(pid);
                    new PejabatDialog(this, true, pejabat).setVisible(true);
                    break;
            }
            refreshData(type);
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin diedit.", "Info", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelected(String type) {
        int selectedRow = -1;
        if ("desa".equals(type)) selectedRow = tableDesa.getSelectedRow();
        else if ("warga".equals(type)) selectedRow = tableWarga.getSelectedRow();
        else if ("pejabat".equals(type)) selectedRow = tablePejabat.getSelectedRow();

        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = -1;
                if ("desa".equals(type)) id = (int) modelDesa.getValueAt(selectedRow, 0);
                else if ("warga".equals(type)) id = (int) modelWarga.getValueAt(selectedRow, 0);
                else if ("pejabat".equals(type)) id = (int) modelPejabat.getValueAt(selectedRow, 0);

                boolean success = false;
                switch (type) {
                    case "desa": success = controller.deleteDesa(id); break;
                    case "warga": success = controller.deleteWarga(id); break;
                    case "pejabat": success = controller.deletePejabat(id); break;
                }

                if (success) {
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    refreshData(type);
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus.", "Info", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshData(String type) {
        switch (type) {
            case "desa": loadDesaData(); break;
            case "warga": loadWargaData(); break;
            case "pejabat": loadPejabatData(); break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            if (Constants.CURRENT_USER != null && "ADMIN".equals(Constants.CURRENT_USER.getRole())) {
                new AdminDashboardView().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Akses ditolak. Hanya admin yang bisa membuka ini.");
            }
        });
    }
}