// src/pendataankecamatan/dialogs/DesaDialog.java
package pendataankecamatan.dialogs;

import com.formdev.flatlaf.FlatLightLaf;
import pendataankecamatan.controller.AdminController;
import pendataankecamatan.model.Desa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class DesaDialog extends JFrame {
    private JTextField fieldNama, fieldKodePos;
    private JButton btnSave, btnCancel;
    private JButton closeBtn, minimizeBtn, maximizeBtn;
    private AdminController controller;
    private Desa currentDesa;
    private Point initialClick;
    private static final int CORNER_RADIUS = 20;

    public DesaDialog(Frame parent, boolean modal, Desa desa) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 30);
            UIManager.put("Component.arc", 15);
            UIManager.put("TextComponent.arc", 25);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setUndecorated(true);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(new Color(0, 0, 0, 0));
        setLayout(new BorderLayout());

        this.currentDesa = desa;
        this.controller = new AdminController();
        setTitle(currentDesa == null ? "Tambah Desa" : "Edit Desa");

        initializeUI();
        applyWindowShape();

        // Add drag functionality
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
                g2d.setColor(new Color(0x006315)); // Hijau
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                g2d.dispose();
            }
        };
        mainContainer.setOpaque(false);

        // Title bar with macOS buttons
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setPreferredSize(new Dimension(0, 40));
        titleBar.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        // macOS buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        controlPanel.setOpaque(false);

        closeBtn = createMacOSButton(new Color(0xFF5F57), "Close");
        minimizeBtn = createMacOSButton(new Color(0xFFBD2E), "Minimize");
        maximizeBtn = createMacOSButton(new Color(0x28CA42), "Maximize");

        controlPanel.add(closeBtn);
        controlPanel.add(minimizeBtn);
        controlPanel.add(maximizeBtn);

        titleBar.add(controlPanel, BorderLayout.WEST);

        // Center title
        JLabel titleLabel = new JLabel(currentDesa == null ? "Tambah Desa" : "Edit Desa", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleBar.add(titleLabel, BorderLayout.CENTER);

        mainContainer.add(titleBar, BorderLayout.NORTH);

        // 🔹 INISIALISASI DULU INPUT FIELDNYA
        fieldNama = new JTextField(15);
        fieldKodePos = new JTextField(10);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Nama Desa
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nama Desa:"), gbc);
        gbc.gridx = 1;
        formPanel.add(createStyledTextField(fieldNama), gbc); // ✅ Sekarang fieldNama sudah ada

        // Kode Pos
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Kode Pos:"), gbc);
        gbc.gridx = 1;
        formPanel.add(createStyledTextField(fieldKodePos), gbc); // ✅ fieldKodePos juga sudah ada

        mainContainer.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        btnSave = createStyledButton("Simpan");
        btnCancel = createStyledButton("Batal");

        btnSave.addActionListener(e -> saveDesa());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        mainContainer.add(buttonPanel, BorderLayout.SOUTH);
        add(mainContainer);

        populateFields();
    }

    private JPanel createStyledTextField(JTextField field) {
        JPanel container = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0x004d10)); // Hijau gelap
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2d.dispose();
            }
        };
        container.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        container.add(field, BorderLayout.CENTER);
        return container;
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
        btn.setPreferredSize(new Dimension(100, 35));
        
        return btn;
    }

    private void populateFields() {
        if (currentDesa != null) {
            fieldNama.setText(currentDesa.getNama());
            fieldKodePos.setText(currentDesa.getKodePos());
        }
    }

    private void saveDesa() {
        String nama = fieldNama.getText().trim();
        String kodePos = fieldKodePos.getText().trim();

        if (nama.isEmpty() || kodePos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama dan Kode Pos harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (currentDesa == null) {
            currentDesa = new Desa(0, nama, kodePos);
        } else {
            currentDesa.setNama(nama);
            currentDesa.setKodePos(kodePos);
        }

        boolean success = controller.updateDesa(currentDesa);
        if (success) {
            JOptionPane.showMessageDialog(this, "Data desa berhasil disimpan.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data desa.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DesaDialog(null, true, null).setVisible(true);
        });
    }
}