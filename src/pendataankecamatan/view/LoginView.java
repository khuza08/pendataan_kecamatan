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

/**
 * Tampilan login untuk aplikasi Pendataan Kecamatan.
 * Menggunakan layout 2 kolom: kiri (logo/info), kanan (form login).
 * Menambahkan rounded corner dan tombol kontrol macOS.
 * Menggunakan FlatLaf Look and Feel untuk tampilan modern.
 */
public class LoginView extends JFrame {

    private JTextField fieldUsername;
    private JPasswordField fieldPassword;
    private JButton buttonLogin;
    private JButton buttonBatal;
    private Point initialClick; // Untuk fitur draggable window

    public LoginView() {
        try {
            // Set FlatLaf Look and Feel
            UIManager.setLookAndFeel(new FlatLightLaf());
            // Konfigurasi komponen rounded
            UIManager.put("Button.arc", 15); // Contoh: membuat button lebih rounded
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 15); // Membuat JTextField/JPasswordField rounded
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Gagal set FlatLaf Look and Feel");
        }

        setUndecorated(true); // Hapus border default window
        setSize(800, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set background JFrame menjadi transparan untuk window tanpa dekorasi
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

        // Styling tombol login dan batal (warna teks bisa diatur via UIManager atau langsung)
        buttonLogin.setFont(new Font("Arial", Font.BOLD, 14));
        buttonLogin.setForeground(Color.WHITE);
        // FlatLaf akan menangani background rounded sesuai UIManager.put("Button.arc", ...)
        buttonLogin.setContentAreaFilled(true); // Biarkan FlatLaf mengelola fill jika tidak di-override
        buttonLogin.setBorderPainted(false);
        buttonLogin.setFocusPainted(false);

        buttonBatal.setFont(new Font("Arial", Font.BOLD, 14));
        buttonBatal.setForeground(Color.WHITE);
        buttonBatal.setContentAreaFilled(true);
        buttonBatal.setBorderPainted(false);
        buttonBatal.setFocusPainted(false);
    }

    private void setupLayout() {
        // Gunakan panel rounded sebagai komponen utama
        RoundedPanel mainPanel = new RoundedPanel(20);
        // FlatLaf tidak mengelola background window utama, jadi kita tetap perlu ini transparan
        mainPanel.setBackground(new Color(0, 0, 0, 0));
        mainPanel.setLayout(new BorderLayout());

        // Panel Title Bar macOS
        JPanel titleBar = createMacOSTitleBar();
        mainPanel.add(titleBar, BorderLayout.NORTH);

        // Panel utama 2 kolom (kiri dan kanan)
        JPanel contentMain = new JPanel(new BorderLayout());
        contentMain.setOpaque(false);
        contentMain.setBackground(new Color(0, 0, 0, 0));

        // Panel kiri (Logo / Informasi) - Gunakan FlatLaf untuk warna, bukan custom paint untuk background utama
        JPanel panelKiri = createLeftPanel();
        panelKiri.setOpaque(false); // Biarkan background transparan untuk custom paint
        contentMain.add(panelKiri, BorderLayout.WEST);

        // Panel kanan (Form Login) - Gunakan FlatLaf untuk warna, bukan custom paint untuk background utama
        JPanel panelKanan = createRightPanel();
        panelKanan.setOpaque(false); // Biarkan background transparan untuk custom paint
        contentMain.add(panelKanan, BorderLayout.CENTER);

        mainPanel.add(contentMain, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createMacOSTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setBackground(new Color(0, 0, 0, 0));
        titleBar.setPreferredSize(new Dimension(0, 30));
        titleBar.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controlPanel.setOpaque(false);
        controlPanel.setBackground(new Color(0, 0, 0, 0));

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
        // Panel kiri digambar secara custom untuk warna latar belakang abu-abu
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(240, 240, 240)); // Warna latar belakang abu-abu
                // Gambar area kiri dengan rounded corner
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Gambar sedikit overlap ke kanan untuk efek seamless
                g2d.fillRect(0, 0, 10, getHeight());
                g2d.dispose();
                // Panggil super agar komponen anak (logo, tagline) tetap digambar dengan FlatLaf styling
                super.paintComponent(g);
            }
        };
        // Karena digambar sendiri, set opaqueness ke false agar tidak menggambar background default
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel labelLogo = new JLabel("KECAMATAN", SwingConstants.CENTER);
        // FlatLaf akan mengatur font dan warna sesuai tema
        labelLogo.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Gunakan font yang umum
        labelLogo.setForeground(new Color(70, 130, 180));
        panel.add(labelLogo, BorderLayout.NORTH);

        JLabel labelTagline = new JLabel("<html><center>Aplikasi Pendataan<br>dan Layanan Masyarakat</center></html>");
        labelTagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelTagline.setForeground(Color.GRAY);
        panel.add(labelTagline, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRightPanel() {
        // Panel kanan digambar secara custom untuk warna latar belakang putih
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE); // Warna latar belakang putih
                // Gambar area kanan dengan rounded corner
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Gambar sedikit overlap ke kiri untuk efek seamless
                g2d.fillRect(getWidth() - 10, 0, 10, getHeight());
                g2d.dispose();
                // Panggil super agar komponen anak (label, field, button) tetap digambar dengan FlatLaf styling
                super.paintComponent(g);
            }
        };
        // Karena digambar sendiri, set opaqueness ke false
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel labelJudul = new JLabel("Masuk ke Akun Anda");
        // FlatLaf akan mengatur font dan warna sesuai tema
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

        JPanel panelButton = new JPanel();
        panelButton.setOpaque(false);
        panelButton.setBackground(new Color(0, 0, 0, 0));
        panelButton.add(buttonLogin);
        panelButton.add(buttonBatal);
        panel.add(panelButton, gbc);

        return panel;
    }

    private void setupEventHandlers() {
        buttonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        buttonBatal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(
                    LoginView.this,
                    "Apakah Anda yakin ingin keluar?",
                    "Konfirmasi",
                    JOptionPane.YES_NO_OPTION
                );
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
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

        // TODO: Ganti dengan panggilan ke AuthController
        if ("admin".equals(username) && "password".equals(password)) {
            JOptionPane.showMessageDialog(this, "Login Berhasil!", "Info", JOptionPane.INFORMATION_MESSAGE);
            // new HomeView().setVisible(true);
            // this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Inner class untuk panel dengan rounded corner (untuk window utama)
    private class RoundedPanel extends JPanel {
        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false); // Panel digambar sendiri
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Gambar area utama dengan rounded corner (warna putih untuk window)
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

            // Gambar border tipis (opsional)
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