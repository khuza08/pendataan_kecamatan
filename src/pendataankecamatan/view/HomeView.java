// src/pendataankecamatan/view/HomeView.java
package pendataankecamatan.view;

import com.formdev.flatlaf.FlatLightLaf;
import pendataankecamatan.controller.AuthController;
import pendataankecamatan.util.Constants;
import pendataankecamatan.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HomeView extends JFrame {
    private JPanel mainPanel;
    private JPanel buttonPanel;
    private Point initialClick;
    private final AuthController authController = new AuthController();

    public HomeView() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 30);
            UIManager.put("Component.arc", 15);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setUndecorated(true);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(new Color(0, 0, 0, 0));

        initializeUI();
        refreshButtons(); 
    }

    private void initializeUI() {
        mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        mainPanel.setOpaque(false);

        // Title bar custom
        JPanel titleBar = createTitleBar();
        mainPanel.add(titleBar, BorderLayout.NORTH);

        // Content
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        JLabel titleLabel = new JLabel("Menu Utama", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 130, 180));
        content.add(titleLabel, BorderLayout.NORTH);

        buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 40, 80));
        content.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);
        setContentPane(mainPanel);
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
        dot.addActionListener(e -> System.exit(0));
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

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(70, 130, 180));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void refreshButtons() {
        buttonPanel.removeAll();

        if (Constants.CURRENT_USER == null) {
            // 🔹 TAMU
            JButton btnInfo = createStyledButton("Lihat Informasi Umum");
            JButton btnLogin = createStyledButton("Login");

            btnInfo.addActionListener(e -> new PublicInfoView().setVisible(true));
            btnLogin.addActionListener(e -> {
                new LoginView().setVisible(true);
                this.dispose();
            });

            buttonPanel.add(btnInfo);
            buttonPanel.add(btnLogin);
        } else {
            User user = Constants.CURRENT_USER;

            // 🔐 WARGA / 👑 ADMIN
            JButton btnLaporan = createStyledButton("Laporan Masalah");
            JButton btnPendataan = createStyledButton("Pendataan Warga");
            JButton btnKonsultasi = createStyledButton("Konsultasi Kesehatan");
            JButton btnLogout = createStyledButton("Logout");

            btnLaporan.addActionListener(e -> new ReportFormView().setVisible(true));
            btnPendataan.addActionListener(e -> new RegistrationFormView().setVisible(true));
            btnKonsultasi.addActionListener(e -> new ChatView().setVisible(true));
            btnLogout.addActionListener(e -> {
                authController.logout();
                refreshButtons(); 
                JOptionPane.showMessageDialog(this, "Berhasil logout.");
            });

            buttonPanel.add(btnLaporan);
            buttonPanel.add(btnPendataan);
            buttonPanel.add(btnKonsultasi);
            buttonPanel.add(btnLogout);

            if ("ADMIN".equals(user.getRole())) {
                JButton btnAdmin = createStyledButton("Admin Dashboard");
                btnAdmin.setBackground(new Color(120, 80, 200)); // warna berbeda
                btnAdmin.addActionListener(e -> new AdminDashboardView().setVisible(true));
                buttonPanel.add(btnAdmin);
            }
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
}