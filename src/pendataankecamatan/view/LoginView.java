// src/pendataankecamatan/view/LoginView.java
package pendataankecamatan.view;

import com.formdev.flatlaf.FlatLightLaf;
import pendataankecamatan.model.User;
import pendataankecamatan.service.DatabaseService;
import pendataankecamatan.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class LoginView extends JFrame {
    private JTextField fieldUsername;
    private JPasswordField fieldPassword;
    private JButton buttonLogin;
    private JButton buttonBatal;
    private JButton togglePasswordButton;
    private Point initialClick;
    private static final int CORNER_RADIUS = 20;
    private JSplitPane splitPane;

    public LoginView() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 30);
            UIManager.put("TextComponent.arc", 25); // 🔹 Arc untuk input field
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
        JPanel macOSButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        macOSButtons.setOpaque(false); // Transparan agar menyatu dengan hijau
        macOSButtons.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0)); // Padding atas dan kiri

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
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/assets/sidoarjo.png")); // 🔹 FIX PATH
            if (logoIcon.getImage() == null) {
                throw new NullPointerException("Logo image not found");
            }
            Image img = logoIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            logo = new JLabel(new ImageIcon(img));
            logo.setHorizontalAlignment(SwingConstants.CENTER);
            
            // 🔹 Tambahkan padding atas & bawah
            logo.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
            
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
            logo = new JLabel("SIDOARJO", SwingConstants.CENTER);
            logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
            logo.setForeground(Color.WHITE);
            logo.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0)); // tetap kasih border untuk konsistensi
        }

        logoContainer.add(logo, gbcLogo);
        leftPanel.add(logoContainer, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);

        // ================= PANEL KANAN (Putih dengan Form) =================
        JPanel rightPanel = new JPanel(new GridBagLayout()) {
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

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 20, 8, 20); // padding konsisten
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Title
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Masuk ke Akun Anda", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        rightPanel.add(titleLabel, gbc);

        gbc.gridy++;
        rightPanel.add(Box.createVerticalStrut(20), gbc);

        // 🔹 Username field dengan background
        gbc.gridy++;
        JPanel usernameContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                g2d.setColor(new Color(0x004d10)); // hijau gelap
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                g2d.dispose();
            }
        };
        usernameContainer.setOpaque(false);
        usernameContainer.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        fieldUsername = new JTextField(15) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        fieldUsername.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12)); // padding kiri dan kanan
        fieldUsername.setOpaque(false);
        fieldUsername.setForeground(new Color(204, 204, 204)); // 80% white (0xCC) saat kosong
        fieldUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fieldUsername.setCaretColor(Color.WHITE);
        fieldUsername.setText("Username");

        setupPlaceholder(fieldUsername, "Username");
        // 🔹 Pindahkan listener ke bawah setelah passwordField dibuat
        // fieldUsername.addActionListener(e -> passwordField.requestFocus());

        usernameContainer.add(fieldUsername, BorderLayout.CENTER);
        rightPanel.add(usernameContainer, gbc);

        gbc.gridy++;
        rightPanel.add(Box.createVerticalStrut(10), gbc); // jarak antar field

        // 🔹 Password field dengan background dan toggle button
        gbc.gridy++;
        JPanel passwordContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                g2d.setColor(new Color(0x004d10)); // hijau gelap
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                g2d.dispose();
            }
        };
        passwordContainer.setOpaque(false);
        passwordContainer.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        fieldPassword = new JPasswordField(15) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paintComponent(g);
                g2.dispose();
            }
        };
        fieldPassword.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 5)); // padding kiri untuk teks, kanan untuk toggle
        fieldPassword.setOpaque(false);
        fieldPassword.setForeground(new Color(204, 204, 204)); // 80% white (0xCC) saat kosong
        fieldPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fieldPassword.setCaretColor(Color.WHITE);
        fieldPassword.setEchoChar((char) 0);
        fieldPassword.setText("Password");

        setupPasswordPlaceholder(fieldPassword);
        fieldPassword.addActionListener(e -> handleLogin());

        togglePasswordButton = new JButton("👁️") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.fillOval(5, 5, getWidth()-10, getHeight()-10);
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(0x006315));
                    g2.fillOval(5, 5, getWidth()-10, getHeight()-10);
                }
                
                super.paintComponent(g);
                g2.dispose();
            }
        };
        togglePasswordButton.setContentAreaFilled(false);
        togglePasswordButton.setBorderPainted(false);
        togglePasswordButton.setFocusPainted(false);
        togglePasswordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        togglePasswordButton.setPreferredSize(new Dimension(40, 30));
        togglePasswordButton.setForeground(new Color(0x006315));
        togglePasswordButton.setToolTipText("Tampilkan Password");
        togglePasswordButton.addActionListener(e -> togglePasswordVisibility());

        passwordContainer.add(fieldPassword, BorderLayout.CENTER);
        passwordContainer.add(togglePasswordButton, BorderLayout.EAST);

        rightPanel.add(passwordContainer, gbc);

        // 🔹 Sekarang fieldUsername bisa mengakses fieldPassword
        fieldUsername.addActionListener(e -> fieldPassword.requestFocus());

        gbc.gridy++;
        rightPanel.add(Box.createVerticalStrut(20), gbc); // jarak ke tombol

        // Buttons
        gbc.gridy++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        buttonLogin = new JButton("Login");
        buttonBatal = new JButton("Batal");

        // Styling tombol seperti HomeView
        buttonLogin = createStyledButton("Login");
        buttonBatal = createStyledButton("Batal");

        // Ganti warna tombol batal
        buttonBatal.setBackground(new Color(180, 180, 180));
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

        buttonPanel.add(buttonLogin);
        buttonPanel.add(buttonBatal);

        rightPanel.add(buttonPanel, gbc);

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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
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
        btn.setPreferredSize(new Dimension(120, 40)); 
        
        if ("Batal".equals(text)) {
            btn.setBackground(new Color(180, 180, 180));
        } else {
            btn.addActionListener(e -> handleLogin());
        }
        
        return btn;
    }

    private void togglePasswordVisibility() {
        if (fieldPassword.getEchoChar() == '•') {
            fieldPassword.setEchoChar((char) 0);
            togglePasswordButton.setText("🙈");
            togglePasswordButton.setToolTipText("Sembunyikan Password");
        } else {
            fieldPassword.setEchoChar('•');
            togglePasswordButton.setText("👁️");
            togglePasswordButton.setToolTipText("Tampilkan Password");
        }
        fieldPassword.requestFocus();
    }

    private void setupPlaceholder(JTextField field, String placeholder) {
        field.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE); 
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(204, 204, 204)); 
                }
            }
        });
    }

    private void setupPasswordPlaceholder(JPasswordField field) {
        field.addFocusListener(new java.awt.event.FocusListener() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals("Password")) {
                    field.setText("");
                    field.setForeground(Color.WHITE); 
                    field.setEchoChar('•');
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setEchoChar((char) 0);
                    field.setText("Password");
                    field.setForeground(new Color(204, 204, 204));
                }
            }
        });
    }

    private void handleLogin() {
        String username = fieldUsername.getText().trim();
        String password = new String(fieldPassword.getPassword());

        if (username.equals("Username") || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            fieldUsername.requestFocus();
            return;
        }

        if (new String(fieldPassword.getPassword()).equals("Password") || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            fieldPassword.requestFocus();
            return;
        }

        DatabaseService dbService = new DatabaseService();
        User user = dbService.authenticateUser(username, password);

        if (user != null) {
            // Simpan user ke sesi global
            Constants.CURRENT_USER = user;

            JOptionPane.showMessageDialog(this, "Login berhasil sebagai: " + user.getNamaLengkap(), "Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Buka HomeView dan tutup LoginView
            SwingUtilities.invokeLater(() -> {
                new HomeView().setVisible(true);
                dispose();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}