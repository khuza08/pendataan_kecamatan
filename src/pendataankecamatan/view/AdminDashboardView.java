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
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class AdminDashboardView extends JFrame {
    private JPanel panelDesa, panelWarga, panelPejabat;
    private JTable tableDesa, tableWarga, tablePejabat;
    private DefaultTableModel modelDesa, modelWarga, modelPejabat;
    private AdminController controller;
    private Point initialClick;
    private static final int CORNER_RADIUS = 20;

    private CardLayout cardLayout;
    private JPanel mainContent;

    public AdminDashboardView() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUndecorated(true);
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0));
        setLayout(new BorderLayout());

        controller = new AdminController();

        initializeUI();
        loadAllData();
        applyWindowShape();
    }

    private void applyWindowShape() {
        if (getWidth() > 0 && getHeight() > 0) {
            Shape shape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
            setShape(shape);
        }
    }

    private void initializeUI() {
        // Main container with rounded corners
        JPanel mainContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0x006315));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                g2d.dispose();
            }
        };
        mainContainer.setOpaque(false);

        // 🔹 Create sidebar panel with full height (no gap at top)
        JPanel sidebar = createSidebar();
        sidebar.setPreferredSize(new Dimension(180, 0));

        // 🔹 macOS buttons directly on sidebar (menyatu dengan background sidebar)
        JPanel macOSButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        macOSButtons.setOpaque(false); // Transparan agar menyatu
        macOSButtons.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 0)); // Padding atas dan kiri

        JButton closeBtn = createMacOSButton(new Color(0xFF5F57), "Close");
        JButton minimizeBtn = createMacOSButton(new Color(0xFFBD2E), "Minimize");
        JButton maximizeBtn = createMacOSButton(new Color(0x28CA42), "Maximize");

        macOSButtons.add(closeBtn);
        macOSButtons.add(minimizeBtn);
        macOSButtons.add(maximizeBtn);

        // Add macOS buttons to sidebar at the top
        sidebar.add(macOSButtons, BorderLayout.NORTH);

        // Create content panels
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);

        panelDesa = createCrudPanel("Desa", "desa");
        panelWarga = createCrudPanel("Warga", "warga");
        panelPejabat = createCrudPanel("Pejabat", "pejabat");

        mainContent.add(createWelcomePanel(), "beranda");
        mainContent.add(panelDesa, "desa");
        mainContent.add(panelWarga, "warga");
        mainContent.add(panelPejabat, "pejabat");

        // Split pane untuk sidebar dan main content
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, mainContent);
        splitPane.setDividerLocation(180);
        splitPane.setDividerSize(0);
        splitPane.setOpaque(false);
        splitPane.setBackground(new Color(0, 0, 0, 0));
        splitPane.setBorder(null);

        mainContainer.add(splitPane, BorderLayout.CENTER);
        add(mainContainer);

        // 🔹 Tambahkan drag functionality ke main container DAN sidebar
        addDragFunctionality(mainContainer);
        addDragFunctionality(sidebar);
    }

    private void addDragFunctionality(Component component) {
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Cegah drag jika klik tombol
                Component clickedComponent = component.getComponentAt(e.getPoint());
                if (!(clickedComponent instanceof JButton)) {
                    initialClick = e.getPoint();
                }
            }
        });

        component.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (initialClick != null) {
                    int newX = e.getXOnScreen() - initialClick.x;
                    int newY = e.getYOnScreen() - initialClick.y;
                    setLocation(newX, newY);
                }
            }
        });
    }

    private JButton createMacOSButton(Color color, String action) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                g2d.setColor(color);
                g2d.fillOval(0, 0, getWidth(), getHeight());
                
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                }
                
                g2d.dispose();
            }
        };

        button.setPreferredSize(new Dimension(12, 12));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if ("Close".equals(action)) {
            button.addActionListener(e -> dispose());
        } else if ("Minimize".equals(action)) {
            button.addActionListener(e -> setState(JFrame.ICONIFIED));
        } else if ("Maximize".equals(action)) {
            button.addActionListener(e -> {
                if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    setExtendedState(JFrame.NORMAL);
                } else {
                    setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
            });
        }

        return button;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(0x004d00)); // Hijau tua
        sidebar.setPreferredSize(new Dimension(180, 0));

        // Panel untuk tombol dengan padding top lebih besar (untuk space setelah macOS buttons)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(new Color(0x004d00));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20)); // Top padding 30px

        // Header Beranda
        buttonPanel.add(createSidebarButton("Beranda", e -> showPanel("beranda")));
        buttonPanel.add(Box.createVerticalStrut(10));

        // Header Manajemen
        JLabel manajemenLabel = new JLabel("Manajemen");
        manajemenLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        manajemenLabel.setForeground(Color.WHITE);
        manajemenLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        buttonPanel.add(manajemenLabel);

        // Tombol Data Warga
        buttonPanel.add(createSidebarButton("Data Warga", e -> showPanel("warga")));
        buttonPanel.add(Box.createVerticalStrut(10));

        // Tombol Data Desa
        buttonPanel.add(createSidebarButton("Data Desa", e -> showPanel("desa")));
        buttonPanel.add(Box.createVerticalStrut(10));

        // Tombol Data Pejabat
        buttonPanel.add(createSidebarButton("Data Pejabat", e -> showPanel("pejabat")));

        sidebar.add(buttonPanel, BorderLayout.CENTER);

        return sidebar;
    }

    private JButton createSidebarButton(String text, ActionListener listener) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Background putih dengan rounded corner
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Teks hijau
                g2d.setColor(new Color(0x006315));
                g2d.setFont(getFont().deriveFont(Font.BOLD, 14f));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };

        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);

        return button;
    }

    private void showPanel(String panelName) {
        cardLayout.show(mainContent, panelName);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel("Selamat Datang di Dashboard Admin");
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        label.setForeground(Color.WHITE);
        panel.add(label);
        panel.setBackground(new Color(0x006315));
        return panel;
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

        String[] columns = getColumnsForEntity(type);
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
        modelDesa.setRowCount(0);
        List<Desa> list = controller.getAllDesa();
        for (Desa item : list) {
            modelDesa.addRow(new Object[]{item.getId(), item.getNama(), item.getKodePos()});
        }
    }

    private void loadWargaData() {
        modelWarga.setRowCount(0);
        List<Warga> list = controller.getAllWarga();
        for (Warga item : list) {
            modelWarga.addRow(new Object[]{
                item.getId(), item.getUserId(), item.getNik(), item.getNamaLengkap(),
                item.getAlamat(), item.getRt(), item.getRw(), item.getDesaId(),
                item.getJenisKelamin(), item.getTanggalLahir()
            });
        }
    }

    private void loadPejabatData() {
        modelPejabat.setRowCount(0);
        List<Pejabat> list = controller.getAllPejabat();
        for (Pejabat item : list) {
            modelPejabat.addRow(new Object[]{item.getId(), item.getNama(), item.getJabatan(), item.getNomorTelepon()});
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