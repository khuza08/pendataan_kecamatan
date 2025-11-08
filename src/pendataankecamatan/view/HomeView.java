// src/pendataankecamatan/view/HomeView.java
package pendataankecamatan.view;

import com.formdev.flatlaf.FlatLightLaf;
import pendataankecamatan.controller.AuthController;
import pendataankecamatan.util.Constants;
import pendataankecamatan.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class HomeView extends JFrame {
    private JPanel rightPanel;
    private Point initialClick;
    private final AuthController authController = new AuthController();
    private static final int CORNER_RADIUS = 20;
    private JSplitPane splitPane;

    public HomeView() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 30);
            UIManager.put("Component.arc", 15);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setUndecorated(true);
        setSize(720, 384);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(new Color(0, 0, 0, 0));
        setLayout(new BorderLayout());

        initializeUI();
        refreshButtons();
        
        // Apply window shape for smooth rounded corners
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyWindowShape();
            }
        });
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
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Background with rounded corners (hijau)
                g2d.setColor(new Color(0x006315));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                
                g2d.dispose();
            }
        };
        mainContainer.setOpaque(false);

        // Split pane untuk 2 kolom
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(360); // 50% dari 720px
        splitPane.setDividerSize(0);
        splitPane.setOpaque(false);
        splitPane.setBackground(new Color(0, 0, 0, 0));

        // ================= PANEL KIRI (Hijau dengan Logo dan Title Bar) =================
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);

        // 🔹 macOS buttons di atas logo, menyatu dengan background hijau
        JPanel macOSButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        macOSButtons.setOpaque(false); // Transparan agar menyatu dengan hijau
        macOSButtons.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0)); // Padding atas dan kiri

        JButton closeBtn = createMacOSButton(new Color(0xFF5F57), "Close");
        JButton minimizeBtn = createMacOSButton(new Color(0xFFBD2E), "Minimize");
        JButton maximizeBtn = createMacOSButton(new Color(0x28CA42), "Maximize");

        macOSButtons.add(closeBtn);
        macOSButtons.add(minimizeBtn);
        macOSButtons.add(maximizeBtn);

        leftPanel.add(macOSButtons, BorderLayout.NORTH); // Letakkan di atas

        // 🔹 Tambahkan logo di tengah panel kiri
        JPanel logoContainer = new JPanel(new GridBagLayout());
        logoContainer.setOpaque(false);

        // Setup GridBagConstraints for centering
        GridBagConstraints gbcLogo = new GridBagConstraints();
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;
        gbcLogo.weightx = 1.0;
        gbcLogo.weighty = 1.0;
        gbcLogo.fill = GridBagConstraints.NONE;
        gbcLogo.anchor = GridBagConstraints.CENTER;

        JLabel logo;
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("../assets/sidoarjo.png")); // 🔹 FIX PATH
            if (logoIcon.getImage() == null) {
                throw new NullPointerException("Logo image not found");
            }
            Image img = logoIcon.getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH);
            logo = new JLabel(new ImageIcon(img));
            logo.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
            logo = new JLabel("SIDOARJO", SwingConstants.CENTER);
            logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
            logo.setForeground(Color.WHITE);
        }

        logoContainer.add(logo, gbcLogo);
        leftPanel.add(logoContainer, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);

        // ================= PANEL KANAN (Putih dengan Menu) =================
        rightPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Background putih dengan rounded corner di kanan
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(-CORNER_RADIUS, 0, getWidth() + CORNER_RADIUS, getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                
                // Tutup bagian kiri yang masih terlihat
                g2d.fillRect(0, 0, CORNER_RADIUS, getHeight());
                
                g2d.dispose();
            }
        };
        rightPanel.setOpaque(false);

        splitPane.setRightComponent(rightPanel);
        mainContainer.add(splitPane, BorderLayout.CENTER);
        add(mainContainer);

        addDragFunctionality();
    }

    private void addDragFunctionality() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
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
            button.addActionListener(e -> System.exit(0));
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

    private JButton createStyledButton(String text) {
    JButton btn = new JButton(text) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            Color bgColor;
            if (getModel().isPressed()) {
                bgColor = new Color(0x004d10);
            } else if (getModel().isRollover()) {
                bgColor = new Color(0x005512);
            } else {
                bgColor = new Color(0x006315);
            }
            
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // ✅ Lebih rapi
            
            super.paintComponent(g);
            g2.dispose();
        }
    };
    
    btn.setContentAreaFilled(false);
    btn.setBorderPainted(false);
    btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.setPreferredSize(new Dimension(250, 45)); // ✅ Lebih ramping
    
    return btn;
}

    public void refreshButtons() {
        rightPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Title
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Menu Utama", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.BLACK);
        rightPanel.add(titleLabel, gbc);

        gbc.gridy++;
        rightPanel.add(Box.createVerticalStrut(20), gbc);

        if (Constants.CURRENT_USER == null) {
            // 🔹 TAMU
            gbc.gridy++;
            JButton btnInfo = createStyledButton("Lihat Informasi Publik");
            btnInfo.addActionListener(e -> {
                new PublicInfoView().setVisible(true);
                this.dispose();
            });
            rightPanel.add(btnInfo, gbc);

            gbc.gridy++;
            JButton btnLogin = createStyledButton("Login");
            btnLogin.addActionListener(e -> {
                new LoginView().setVisible(true);
                this.dispose();
            });
            rightPanel.add(btnLogin, gbc);
        } else {
            User user = Constants.CURRENT_USER;

            // 🔐 WARGA / 👑 ADMIN
            gbc.gridy++;
            JButton btnLaporan = createStyledButton("Laporan Masalah");
            btnLaporan.addActionListener(e -> new ReportFormView().setVisible(true));
            rightPanel.add(btnLaporan, gbc);

            gbc.gridy++;
            JButton btnPendataan = createStyledButton("Pendataan Warga");
            btnPendataan.addActionListener(e -> new RegistrationFormView().setVisible(true));
            rightPanel.add(btnPendataan, gbc);

            gbc.gridy++;
            JButton btnKonsultasi = createStyledButton("Konsultasi Kesehatan");
            btnKonsultasi.addActionListener(e -> new ChatView().setVisible(true));
            rightPanel.add(btnKonsultasi, gbc);

            gbc.gridy++;
            JButton btnLogout = createStyledButton("Logout");
            btnLogout.addActionListener(e -> {
                authController.logout();
                refreshButtons();
                JOptionPane.showMessageDialog(this, "Berhasil logout.");
            });
            rightPanel.add(btnLogout, gbc);

            if ("ADMIN".equals(user.getRole())) {
                gbc.gridy++;
                JButton btnAdmin = createStyledButton("Admin Dashboard");
                btnAdmin.setBackground(new Color(120, 80, 200));
                btnAdmin.addActionListener(e -> new AdminDashboardView().setVisible(true));
                rightPanel.add(btnAdmin, gbc);
            }
        }

        rightPanel.revalidate();
        rightPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HomeView frame = new HomeView();
            frame.setVisible(true);
            frame.applyWindowShape();
        });
    }
}