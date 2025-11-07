// src/pendataankecamatan/view/PublicInfoView.java
package pendataankecamatan.view;

import com.formdev.flatlaf.FlatLightLaf; // ✅ Import FlatLAF
import pendataankecamatan.controller.PublicController;
import pendataankecamatan.model.Desa;
import pendataankecamatan.model.Pejabat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PublicInfoView extends JFrame {
    private final PublicController publicController = new PublicController();

    public PublicInfoView() {
        // 🔹 Atur FlatLAF hanya jika belum diatur sebelumnya
        try {
            if (UIManager.getLookAndFeel().getClass() != FlatLightLaf.class) {
                UIManager.setLookAndFeel(new FlatLightLaf());
                // Set properti FlatLAF (opsional, sesuai preferensimu)
                UIManager.put("Button.arc", 30);
                UIManager.put("Component.arc", 15);
                UIManager.put("Table.showHorizontalLines", true);
                UIManager.put("Table.showVerticalLines", true);
                UIManager.put("Table.gridColor", new Color(230, 230, 230));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Informasi Umum - Kecamatan Siwalan Panji");
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("Profil Kecamatan", createProfilPanel());
        tabbedPane.addTab("Daftar Desa", createDesaPanel());
        tabbedPane.addTab("Pejabat Kecamatan", createPejabatPanel());
        tabbedPane.addTab("Peta Wilayah", createPetaPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createProfilPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea(publicController.getProfilKecamatan());
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        textArea.setBackground(UIManager.getColor("Panel.background")); // cocokkan warna latar
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDesaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"No", "Nama Desa", "Kode Pos"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Desa> desaList = publicController.getDaftarDesa();
        for (int i = 0; i < desaList.size(); i++) {
            Desa d = desaList.get(i);
            model.addRow(new Object[]{i + 1, d.getNama(), d.getKodePos()});
        }

        JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPejabatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = {"No", "Nama", "Jabatan", "Kontak"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Pejabat> pejabatList = publicController.getDaftarPejabat();
        for (int i = 0; i < pejabatList.size(); i++) {
            Pejabat p = pejabatList.get(i);
            model.addRow(new Object[]{i + 1, p.getNama(), p.getJabatan(), p.getNomorTelepon()});
        }

        JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPetaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("<html><center>" +
            "<h3 style='color:#4682B4;'>Peta Wilayah Kecamatan Siwalan Panji</h3>" +
            "<p style='color:gray; font-size:14px; margin-top:20px;'>" +
            "ℹ️ Fitur peta berbasis gambar akan segera hadir.<br>" +
            "<b>Sumber: Pemerintah Kabupaten Sidoarjo</b>" +
            "</p>" +
            "</center></html>", SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}