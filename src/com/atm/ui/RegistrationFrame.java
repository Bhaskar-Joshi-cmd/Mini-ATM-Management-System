package com.atm.ui;

import com.atm.util.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegistrationFrame extends JFrame {

    private JTextField nameTxt, accNumTxt, limitTxt;
    private JPasswordField pinTxt;
    private JComboBox<String> typeCombo;
    private JButton registerBtn, backBtn;

    public RegistrationFrame() {
        setTitle("Mini ATM - New User Registration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 600); // Slightly taller to fit extra fields
        setLocationRelativeTo(null);
        setResizable(false);

        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(new Color(235, 235, 235));

        JPanel regPanel = new JPanel(new GridBagLayout());
        regPanel.setBackground(Color.WHITE);
        regPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("CREATE ACCOUNT", JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(0, 70, 140));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        regPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        Font labelFont = new Font("SansSerif", Font.PLAIN, 14);

        // Name Field
        addFormField(regPanel, "Full Name:", nameTxt = new JTextField(15), 1, gbc, labelFont);
        
        // Account Number Field
        addFormField(regPanel, "Account Number:", accNumTxt = new JTextField(15), 2, gbc, labelFont);

        // Account Type Field (Combo Box)
        JLabel typeLabel = new JLabel("Account Type:");
        typeLabel.setFont(labelFont);
        gbc.gridy = 3; gbc.gridx = 0;
        regPanel.add(typeLabel, gbc);
        typeCombo = new JComboBox<>(new String[]{"Saving", "Current"});
        gbc.gridx = 1;
        regPanel.add(typeCombo, gbc);

        // PIN Field
        addFormField(regPanel, "Set 4-Digit PIN:", pinTxt = new JPasswordField(15), 4, gbc, labelFont);

        // Daily Limit Field
        addFormField(regPanel, "Daily Limit (₹):", limitTxt = new JTextField("10000", 15), 5, gbc, labelFont);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btnPanel.setBackground(Color.WHITE);

        registerBtn = new JButton("REGISTER");
        styleButton(registerBtn, new Color(51, 153, 0)); // Success Green

        backBtn = new JButton("BACK");
        styleButton(backBtn, Color.GRAY);

        btnPanel.add(registerBtn);
        btnPanel.add(backBtn);

        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 5, 5, 5);
        regPanel.add(btnPanel, gbc);

        contentPane.add(regPanel);

        // Action Listeners
        registerBtn.addActionListener(e -> handleRegistration());
        backBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
    }

    private void addFormField(JPanel panel, String label, JTextField field, int y, GridBagConstraints gbc, Font font) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(font);
        gbc.gridy = y; gbc.gridx = 0;
        panel.add(lbl, gbc);
        field.setPreferredSize(new Dimension(0, 30));
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setPreferredSize(new Dimension(120, 35));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
    }

    private void handleRegistration() {
    try {
        String name = nameTxt.getText();
        String accNum = accNumTxt.getText();
        String pin = new String(pinTxt.getPassword());
        String type = typeCombo.getSelectedItem().toString();
        
        // Basic validation to prevent empty fields
        if(name.isEmpty() || accNum.isEmpty() || pin.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
            return;
        }

        double limit = Double.parseDouble(limitTxt.getText());

        com.atm.dao.AccountDAO accDAO = new com.atm.dao.AccountDAO();
        
        if (accDAO.registerNewUser(accNum, name, type, pin, limit)) {
            // 1. Show Success Message
            JOptionPane.showMessageDialog(this, "Registration Successful! Please Login.");
            
            // 2. Open the Login Frame
            new LoginFrame().setVisible(true);
            
            // 3. THIS MAKES THE REGISTRATION PANEL DISAPPEAR
            this.dispose(); 
            
        } else {
            JOptionPane.showMessageDialog(this, "Registration Failed. Account Number might already exist.");
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Please enter a valid numeric limit.");
    }
}}