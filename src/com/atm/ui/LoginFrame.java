package com.atm.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.URL;
import java.sql.*; 
import com.atm.util.DBConnection;
public class LoginFrame extends JFrame {

    private JLabel titleLabel, logoLabel, accountLabel, pinLabel, registerLabel;
    private JTextField accountTextField;
    private JPasswordField pinPasswordField;
    private JButton loginButton, clearButton;

    private final String ACCOUNT_PLACEHOLDER = "Enter Account Number";
    private final String PIN_PLACEHOLDER = "••••";

    public LoginFrame() {
        setTitle("Mini ATM System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 500); 
        setLocationRelativeTo(null);
        setResizable(false);

        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(new Color(235, 235, 235));

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 5, 12, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // A. Welcome Title
        titleLabel = new JLabel("WELCOME TO MINI ATM", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 70, 140));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        // B. Logo Implementation
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        ;
        
        try {
            URL imgUrl = getClass().getResource("/resources/atm_logo.png");
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                logoLabel = new JLabel(new ImageIcon(img));
            } else {
                logoLabel = new JLabel("[ LOGO MISSING ]");
                logoLabel.setForeground(Color.RED);
            }
        } catch (Exception e) {
            logoLabel = new JLabel("ATM");
        }
        loginPanel.add(logoLabel, gbc);

        // C. Input Fields
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);

        accountLabel = new JLabel("Account Number:");
        accountLabel.setFont(labelFont);
        gbc.gridy = 2;
        gbc.gridx = 0;
        loginPanel.add(accountLabel, gbc);

        accountTextField = new JTextField(15);
        accountTextField.setPreferredSize(new Dimension(0, 30));
        accountTextField.addFocusListener(new PlaceholderHandler(accountTextField, ACCOUNT_PLACEHOLDER));
        gbc.gridx = 1;
        loginPanel.add(accountTextField, gbc);

        pinLabel = new JLabel("PIN (4-Digits):");
        pinLabel.setFont(labelFont);
        gbc.gridy = 3;
        gbc.gridx = 0;
        loginPanel.add(pinLabel, gbc);

        pinPasswordField = new JPasswordField(15);
        pinPasswordField.setPreferredSize(new Dimension(0, 30));
        pinPasswordField.addFocusListener(new PlaceholderHandler(pinPasswordField, PIN_PLACEHOLDER));
        gbc.gridx = 1;
        loginPanel.add(pinPasswordField, gbc);

        // D. Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(Color.WHITE);

        loginButton = new JButton("LOGIN");
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);

        clearButton = new JButton("CLEAR");
        clearButton.setPreferredSize(new Dimension(100, 35));
        clearButton.setFocusPainted(false);

        btnPanel.add(loginButton);
        btnPanel.add(clearButton);

        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 5, 15, 5);
        loginPanel.add(btnPanel, gbc);

        // E. Footer
        registerLabel = new JLabel("New User? Register here ", JLabel.CENTER);
        registerLabel.addMouseListener(new java.awt.event.MouseAdapter() {
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        new RegistrationFrame().setVisible(true); // Open Reg Frame
        dispose(); // Close Login Frame
    }
});
        registerLabel.setForeground(new Color(0, 102, 204));
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 5, 5, 5);
        loginPanel.add(registerLabel, gbc);

        contentPane.add(loginPanel);

        // Listeners
        loginButton.addActionListener(e -> handleLogin());
        clearButton.addActionListener(e -> {
            resetField(accountTextField, ACCOUNT_PLACEHOLDER);
            resetField(pinPasswordField, PIN_PLACEHOLDER);
        });
    }

    private void handleLogin() {
        String accNum = accountTextField.getText();
        String pin = new String(pinPasswordField.getPassword());

        if (accNum.equals(ACCOUNT_PLACEHOLDER) || accNum.isEmpty() || pin.equals(PIN_PLACEHOLDER) || pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both Account Number and PIN.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // We only need to check if the Account and PIN match in the login table
        String query = "SELECT account_number FROM login WHERE account_number = ? AND pin = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            pst.setString(1, accNum);
            pst.setString(2, pin);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // SUCCESS: Close login and open the NEW MainFrame
                // We pass the accNum so MainFrame can load Bhaskar's specific data
                new MainFrame(accNum).setVisible(true);
                this.dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Account Number or PIN", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void resetField(JTextField field, String hint) {
        field.setText(hint);
        field.setForeground(Color.GRAY);
        if (field instanceof JPasswordField) ((JPasswordField) field).setEchoChar((char) 0);
    }

    private class PlaceholderHandler implements FocusListener {
        private JTextField field;
        private String hint;
        private char echo;

        public PlaceholderHandler(JTextField f, String h) {
            this.field = f; this.hint = h;
            if (f instanceof JPasswordField) {
                this.echo = ((JPasswordField) f).getEchoChar();
                ((JPasswordField) f).setEchoChar((char) 0);
            }
            f.setText(h);
            f.setForeground(Color.GRAY);
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (field.getText().equals(hint)) {
                field.setText("");
                field.setForeground(Color.BLACK);
                if (field instanceof JPasswordField) ((JPasswordField) field).setEchoChar(echo);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (field.getText().isEmpty()) resetField(field, hint);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new LoginFrame().setVisible(true);
        });
    }
}