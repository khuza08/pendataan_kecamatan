package pendataankecamatan.view;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;

public class LoginView extends JFrame {

    private JTextField fieldUsername;
    private JPasswordField fieldPassword;
    private JButton buttonLogin;
    private JButton buttonBatal;
    private Point initialClick;
    private static final int CORNER_RADIUS = 20;

    public LoginView() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 15);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 15);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setUndecorated(true);
        setSize(800, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getLayeredPane().setOpaque(false);
        getRootPane().setOpaque(false);

        setBackground(new Color(0, 0, 0, 0));
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initializeComponents() {
        fieldUsername = new JTextField(15);
        fieldPassword = new JPasswordField(15);
        buttonLogin = new JButton("Login");
        buttonBatal = new JButton("Batal");

        buttonLogin.setFont(new Font("Arial", Font.BOLD, 14));
        buttonLogin.setForeground(Color.WHITE);
        buttonLogin.setBackground(new Color(70, 130, 180));
        buttonLogin.setContentAreaFilled(true);
        buttonLogin.setBorderPainted(false);
        buttonLogin.setFocusPainted(false);

        buttonBatal.setFont(new Font("Arial", Font.BOLD, 14));
        buttonBatal.setForeground(Color.WHITE);
        buttonBatal.setBackground(new Color(180, 180, 180));
        buttonBatal.setContentAreaFilled(true);
        buttonBatal.setBorderPainted(false);
        buttonBatal.setFocusPainted(false);
    }

    private void setupLayout() {
        RoundedPanel mainPanel = new RoundedPanel(CORNER_RADIUS);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder());

        JPanel titleBar = createMacOSTitleBar();
        mainPanel.add(titleBar, BorderLayout.NORTH);

        JPanel contentMain = new JPanel(new BorderLayout());
        contentMain.setOpaque(false);

        JPanel panelKiri = createLeftPanel();
        contentMain.add(panelKiri, BorderLayout.WEST);

        JPanel panelKanan = createRightPanel();
        contentMain.add(panelKanan, BorderLayout.CENTER);

        mainPanel.add(contentMain, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JPanel createMacOSTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setPreferredSize(new Dimension(0, 40));
        titleBar.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controlPanel.setOpaque(false);

        JButton redDot = createMacOSDotButton(new Color(0xFF5F57), "Close");
        JButton yellowDot = createMacOSDotButton(new Color(0xFFBD2E), "Minimize");
        JButton greenDot = createMacOSDotButton(new Color(0x28CA42), "Maximize");

        controlPanel.add(redDot);
        controlPanel.add(yellowDot);
        controlPanel.add(greenDot);

        titleBar.add(controlPanel, BorderLayout.WEST);

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
                // Jangan panggil super.paintComponent — biar transparan
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
        }

        return dot;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(240, 240, 240));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                // Tutup sisi kanan agar tidak transparan di ujung kiri window
                g2d.fillRect(getWidth() - CORNER_RADIUS, 0, CORNER_RADIUS, getHeight());
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 30, 30, 30));

        JLabel labelLogo = new JLabel("KECAMATAN", SwingConstants.CENTER);
        labelLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        labelLogo.setForeground(new Color(70, 130, 180));
        panel.add(labelLogo, BorderLayout.NORTH);

        JLabel labelTagline = new JLabel("<html><center>Aplikasi Pendataan<br>dan Layanan Masyarakat</center></html>");
        labelTagline.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        labelTagline.setForeground(Color.GRAY);
        labelTagline.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(labelTagline, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                // Tutup sisi kiri agar tidak transparan di ujung kanan window
                g2d.fillRect(0, 0, CORNER_RADIUS, getHeight());
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel labelJudul = new JLabel("Masuk ke Akun Anda");
        labelJudul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(labelJudul, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        panel.add(fieldUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        panel.add(fieldPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(20, 8, 8, 8);

        JPanel panelButton = new JPanel();
        panelButton.setOpaque(false);
        panelButton.add(buttonLogin);
        panelButton.add(buttonBatal);
        panel.add(panelButton, gbc);

        return panel;
    }

    private void setupEventHandlers() {
        buttonLogin.addActionListener(e -> handleLogin());
        buttonBatal.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                LoginView.this,
                "Apakah Anda yakin ingin keluar?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
            );
            if (option == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    private void handleLogin() {
        String username = fieldUsername.getText().trim();
        String password = new String(fieldPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username dan Password harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("admin".equals(username) && "password".equals(password)) {
            JOptionPane.showMessageDialog(this, "Login Berhasil!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // RoundedPanel sebagai content utama — digambar penuh, tidak pakai setShape()
    private class RoundedPanel extends JPanel {
        private final int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (getWidth() <= 0 || getHeight() <= 0) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            // Gambar background putih untuk panel utama
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            // Gambar border
            g2.setColor(new Color(200, 200, 200));
            g2.setStroke(new BasicStroke(1.0f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

            g2.dispose();
        }
        
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}