// src/pendataankecamatan/view/PublicInfoView.java
package pendataankecamatan.view;

import com.formdev.flatlaf.FlatLightLaf; // ✅ Import FlatLAF
import pendataankecamatan.controller.PublicController;
import pendataankecamatan.model.Desa;
import pendataankecamatan.model.Pejabat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class PublicInfoView extends JFrame {
    private final PublicController publicController = new PublicController();
    private Point initialClick;

    public PublicInfoView() {
        // 🔹 Atur FlatLAF hanya jika belum diatur sebelumnya
        try {
            if (UIManager.getLookAndFeel().getClass() != FlatLightLaf.class) {
                UIManager.setLookAndFeel(new FlatLightLaf());
                UIManager.put("Button.arc", 30);
                UIManager.put("Component.arc", 15);
                UIManager.put("Table.showHorizontalLines", true);
                UIManager.put("Table.showVerticalLines", true);
                UIManager.put("Table.gridColor", new Color(230, 230, 230));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 🔹 Set custom window
        setUndecorated(true);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBackground(new Color(0, 0, 0, 0)); // Transparent background
        setLayout(new BorderLayout());

        // 🔹 Create rounded window shape
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));

        // 🔹 Create main content panel with rounded border
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);

        // 🔹 Create title bar
        JPanel titleBar = createTitleBar();
        mainPanel.add(titleBar, BorderLayout.NORTH);

        // 🔹 Add content (tabs)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.putClientProperty("JTabbedPane.tabType", "card"); // Modern FlatLAF style

        tabbedPane.addTab("Profil Kecamatan", createProfilPanel());
        tabbedPane.addTab("Daftar Desa", createDesaPanel());
        tabbedPane.addTab("Pejabat Kecamatan", createPejabatPanel());
        tabbedPane.addTab("Peta Wilayah", createPetaPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // 🔹 Add back button panel at the bottom
        JPanel bottomPanel = createBackButtonPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createBackButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // atas, kiri, bawah, kanan

        JButton backButton = new JButton("Kembali ke Home");
        backButton.addActionListener(e -> {
            new HomeView().setVisible(true);
            dispose(); // Tutup PublicInfoView
        });

        panel.add(backButton);
        return panel;
    }

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setPreferredSize(new Dimension(0, 40));
        titleBar.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        // Panel untuk tombol macOS (red, yellow, green)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controlPanel.setOpaque(false);

        JButton redDot = createMacOSDotButton(new Color(0xFF5F57), "Close");
        JButton yellowDot = createMacOSDotButton(new Color(0xFFBD2E), "Minimize");
        JButton greenDot = createMacOSDotButton(new Color(0x28CA42), "Maximize");

        controlPanel.add(redDot);
        controlPanel.add(yellowDot);
        controlPanel.add(greenDot);

        titleBar.add(controlPanel, BorderLayout.WEST);

        // Agar bisa drag window
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick != null) {
                    int newX = e.getXOnScreen() - initialClick.x;
                    int newY = e.getYOnScreen() - initialClick.y;
                    setLocation(newX, newY);
                }
            }
        });

        return titleBar;
    }

    private JButton createMacOSDotButton(Color color, String action) {
        JButton dot = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                }
                g2d.dispose();
            }
        };

        dot.setPreferredSize(new Dimension(12, 12));
        dot.setMinimumSize(new Dimension(12, 12));
        dot.setMaximumSize(new Dimension(12, 12));

        dot.setContentAreaFilled(false);
        dot.setBorderPainted(false);
        dot.setFocusPainted(false);
        dot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if ("Close".equals(action)) {
            dot.addActionListener(e -> dispose()); // Tutup window ini, bukan aplikasi
        } else if ("Minimize".equals(action)) {
            dot.addActionListener(e -> setState(JFrame.ICONIFIED));
        } else if ("Maximize".equals(action)) {
            dot.addActionListener(e -> {
                if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    setExtendedState(JFrame.NORMAL);
                } else {
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            });
        }

        return dot;
    }

    private JPanel createProfilPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea(publicController.getProfilKecamatan());
        textArea.setEditable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        textArea.setBackground(UIManager.getColor("Panel.background"));
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