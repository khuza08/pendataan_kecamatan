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
            UIManager.put("Button.arc", 30); // pill-shaped
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
        refreshButtons(); // Tampilkan tombol sesuai status login
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

        JLabel titleLabel = new JLabel("Dashboard Utama", SwingConstants.CENTER);
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

    private JPanel createTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setPreferredSize(new Dimension(0, 40));
        titleBar.setOpaque(false);
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
                    setLocation(e.getXOnScreen() - initialClick.x, e.getYOnScreen() - initialClick.y);
                }
            }
        });

        JButton closeButton = new JButton("✕");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 16));
        closeButton.setForeground(Color.RED);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> System.exit(0));

        titleBar.add(closeButton, BorderLayout.EAST);
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
                refreshButtons(); // Kembali ke mode tamu
                JOptionPane.showMessageDialog(this, "Berhasil logout.");
            });

            buttonPanel.add(btnLaporan);
            buttonPanel.add(btnPendataan);
            buttonPanel.add(btnKonsultasi);
            buttonPanel.add(btnLogout);

            // 👑 Tambahkan Admin Dashboard jika admin
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