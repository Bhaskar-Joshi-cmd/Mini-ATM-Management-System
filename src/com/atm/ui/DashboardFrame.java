package com.atm.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.net.URL;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import com.atm.util.DBConnection;
import com.atm.dao.AccountDAO;

public class DashboardFrame extends JFrame {

    private JLabel lblWelcome, lblAccountNum, lblLogo;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private String currentAccountNumber;
    
    private JPanel mainDisplayPanel;
    private CardLayout cardLayout;
    private AccountDAO accountDAO = new AccountDAO();

    public DashboardFrame(String customerName, String accountNumber, double balance) {
        this.currentAccountNumber = accountNumber;
        
        setTitle("Mini ATM System - Dashboard");
        setSize(1050, 850); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(230, 235, 240));

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 100));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        try {
            URL imgUrl = getClass().getResource("/resources/atm_logo.png");
            if (imgUrl != null) {
                Image img = new ImageIcon(imgUrl).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
                lblLogo = new JLabel(new ImageIcon(img));
                lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
                header.add(lblLogo, BorderLayout.WEST);
            }
        } catch (Exception e) { }

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);
        lblWelcome = new JLabel("WELCOME, " + customerName.toUpperCase());
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblWelcome.setForeground(new Color(0, 70, 140));
        lblAccountNum = new JLabel("Account Number: " + accountNumber);
        infoPanel.add(lblWelcome);
        infoPanel.add(lblAccountNum);
        header.add(infoPanel, BorderLayout.CENTER);

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel(new GridLayout(5, 1, 0, 15));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setOpaque(false);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        JButton btnCheckBalance = createStyledButton("CHECK BALANCE");
        JButton btnWithdraw = createStyledButton("WITHDRAW");
        JButton btnDeposit = createStyledButton("DEPOSIT");
        JButton btnHistory = createStyledButton("TRANSACTION HISTORY");
        JButton btnLogout = createStyledButton("LOGOUT");

        sidebar.add(btnCheckBalance);
        sidebar.add(btnWithdraw);
        sidebar.add(btnDeposit);
        sidebar.add(btnHistory);
        sidebar.add(btnLogout);

        // --- MAIN CENTER AREA ---
        cardLayout = new CardLayout();
        mainDisplayPanel = new JPanel(cardLayout);
        mainDisplayPanel.setOpaque(false);

        mainDisplayPanel.add(createPlaceholderPanel("Please select an option from the sidebar"), "WELCOME");

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setBorder(BorderFactory.createTitledBorder("Last 5 Transactions"));
        String[] cols = {"Date/Time", "Action", "Amount", "Balance After"};
        tableModel = new DefaultTableModel(cols, 0);
        transactionTable = new JTable(tableModel);
        transactionTable.setFillsViewportHeight(true);
        historyPanel.add(new JScrollPane(transactionTable), BorderLayout.CENTER);

        mainDisplayPanel.add(historyPanel, "HISTORY");
        mainDisplayPanel.add(createPlaceholderPanel("Cash Withdrawal Screen"), "WITHDRAW_SCREEN");
        mainDisplayPanel.add(createPlaceholderPanel("Cash Deposit Screen"), "DEPOSIT_SCREEN");

        btnCheckBalance.addActionListener(e -> {
            mainDisplayPanel.add(createBalancePanel(), "BALANCE"); 
            cardLayout.show(mainDisplayPanel, "BALANCE");
        });
        btnWithdraw.addActionListener(e -> cardLayout.show(mainDisplayPanel, "WITHDRAW_SCREEN"));
        btnDeposit.addActionListener(e -> cardLayout.show(mainDisplayPanel, "DEPOSIT_SCREEN"));
        btnHistory.addActionListener(e -> {
            loadTransactionData();
            cardLayout.show(mainDisplayPanel, "HISTORY");
        });
        
        btnLogout.addActionListener(e -> { 
            this.dispose(); 
            new LoginFrame().setVisible(true); 
        });

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(new Color(230, 235, 240));
        footer.add(new JLabel("Admin Portal: Manage Accounts"));

        add(header, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(mainDisplayPanel, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
        
        cardLayout.show(mainDisplayPanel, "WELCOME");
    }

    private JPanel createBalancePanel() {
        Map<String, Object> data = accountDAO.getAccountSummary(currentAccountNumber);
        
        JPanel container = new JPanel(new BorderLayout(0, 15));
        container.setBackground(new Color(230, 235, 240));
        container.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("ACCOUNT SUMMARY: " + data.getOrDefault("type", "Savings"), JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28)); 
        title.setForeground(new Color(0, 51, 102));
        title.setOpaque(true);
        title.setBackground(Color.WHITE);
        title.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 0, 10, 0)));
        container.add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 25, 25));
        grid.setOpaque(false);

        // BLOCK 1: Balance
        JPanel b1 = createInfoBox("CURRENT AVAILABLE BALANCE");
        b1.setLayout(new BoxLayout(b1, BoxLayout.Y_AXIS));
        
        JLabel bVal = new JLabel("$" + String.format("%.2f", data.getOrDefault("balance", 0.0)));
        bVal.setFont(new Font("SansSerif", Font.BOLD, 42));
        bVal.setForeground(new Color(0, 102, 0));
        bVal.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hide = new JLabel("👁 Hide Balance");
        hide.setForeground(Color.GRAY);
        hide.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnPrint = new JButton("PRINT RECEIPT");
        btnPrint.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setBackground(new Color(102, 153, 204));
        btnPrint.setOpaque(true);
        btnPrint.setBorderPainted(false);
        btnPrint.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnPrint.setPreferredSize(new Dimension(240, 55));
        btnPrint.setMaximumSize(new Dimension(240, 55));

        b1.add(Box.createVerticalGlue());
        b1.add(bVal);
        b1.add(hide);
        b1.add(Box.createVerticalStrut(30)); 
        b1.add(btnPrint);
        b1.add(Box.createVerticalGlue());

        // BLOCK 2: Daily Limits
        JPanel b2 = createInfoBox("DAILY LIMIT STATUS");
        b2.add(Box.createVerticalStrut(20));
        b2.add(createLimitItem("ATM WITHDRAWAL", 500, 2000));
        b2.add(Box.createVerticalStrut(20)); 
        b2.add(createLimitItem("POS TRANSACTIONS", 1200, 5000));

        // BLOCK 3: Financial Trend
        JPanel b3 = createInfoBox("FINANCIAL TREND");
        b3.add(new TrendGraphPanel(currentAccountNumber));

        // BLOCK 4: Security Notes (FIXED DUPLICATE TITLE & ALIGNMENT)
        JPanel b4 = createInfoBox("SECURITY NOTES");
        // No need to set Layout here as createInfoBox already uses BoxLayout(Y_AXIS)
        
        JPanel secContent = new JPanel();
        secContent.setLayout(new BoxLayout(secContent, BoxLayout.Y_AXIS));
        secContent.setOpaque(false);
        // Indent 20 pixels from the left to match the Daily Limit block
        secContent.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 0)); 

        JLabel secStatus = new JLabel("🛡 ACCOUNT ACTIVE & SECURE");
        secStatus.setFont(new Font("SansSerif", Font.BOLD, 18));
        secStatus.setForeground(new Color(0, 128, 0));
        secStatus.setAlignmentX(Component.LEFT_ALIGNMENT); // FORCE LEFT

        JLabel loginDate = new JLabel("Last login: " + data.getOrDefault("last_login", "N/A"));
        loginDate.setFont(new Font("SansSerif", Font.PLAIN, 15));
        loginDate.setAlignmentX(Component.LEFT_ALIGNMENT); // FORCE LEFT

        secContent.add(secStatus);
        secContent.add(Box.createVerticalStrut(15)); 
        secContent.add(loginDate);
        
        b4.add(secContent);

        grid.add(b1); grid.add(b2); grid.add(b3); grid.add(b4);
        container.add(grid, BorderLayout.CENTER);
        
        return container;
    }

    private JPanel createLimitItem(String label, int spent, int total) {
        JPanel p = new JPanel(new BorderLayout(2, 2)); 
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(400, 70));
        p.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0)); 
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16)); 
        
        JProgressBar bar = new JProgressBar(0, total);
        bar.setValue(spent);
        bar.setForeground(new Color(70, 130, 255));
        bar.setPreferredSize(new Dimension(200, 18));
        
        JLabel stats = new JLabel("$" + spent + " / $" + total);
        stats.setFont(new Font("SansSerif", Font.PLAIN, 14));

        p.add(lbl, BorderLayout.NORTH);
        p.add(bar, BorderLayout.CENTER);
        p.add(stats, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createInfoBox(String title) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 10, 15, 10)));
        
        JLabel t = new JLabel(title, JLabel.CENTER);
        t.setFont(new Font("SansSerif", Font.BOLD, 20)); 
        t.setAlignmentX(Component.CENTER_ALIGNMENT);
        box.add(t);
        return box;
    }

    class TrendGraphPanel extends JPanel {
        private java.util.List<Double> balances = new ArrayList<>();
        public TrendGraphPanel(String acc) {
            this.setBackground(Color.WHITE);
            fetchTrendData(acc);
        }

        private void fetchTrendData(String acc) {
            String q = "SELECT new_balance FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC LIMIT 6";
            try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(q)) {
                pst.setString(1, acc);
                ResultSet rs = pst.executeQuery();
                while (rs.next()) balances.add(rs.getDouble("new_balance"));
                Collections.reverse(balances);
            } catch (SQLException e) { }
            if(balances.isEmpty()) { Collections.addAll(balances, 1000.0, 1200.0, 1100.0, 1500.0, 1400.0, 14500.0); }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), m = 40;
            g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2.setColor(Color.GRAY);
            g2.drawString("Balance ($)", 5, m - 10);
            g2.drawString("Time (Recent)", w - 70, h - 10);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(m, h-m, w-m, h-m); 
            g2.drawLine(m, m, m, h-m);     

            double max = Collections.max(balances);
            double min = Collections.min(balances);
            double range = (max == min) ? max : (max - min);
            g2.setColor(new Color(70, 130, 255));
            g2.setStroke(new BasicStroke(3f));
            int step = (w - 2*m) / (balances.size() - 1);
            
            for (int i = 0; i < balances.size() - 1; i++) {
                int y1 = (int)(h - m - ((balances.get(i) - min) / range * (h - 2*m)));
                int y2 = (int)(h - m - ((balances.get(i+1) - min) / range * (h - 2*m)));
                g2.drawLine(m + i*step, y1, m + (i+1)*step, y2);
                g2.fillOval(m + i*step - 4, y1 - 4, 8, 8);
            }
            int lastY = (int)(h - m - ((balances.get(balances.size()-1) - min) / range * (h - 2*m)));
            g2.fillOval(m + (balances.size()-1)*step - 4, lastY - 4, 8, 8);
        }
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        JLabel l = new JLabel(title);
        l.setFont(new Font("SansSerif", Font.BOLD, 20));
        p.add(l);
        return p;
    }

    private void loadTransactionData() {
        String query = "SELECT transaction_date, transaction_type, amount, new_balance FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC LIMIT 5";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, currentAccountNumber);
            ResultSet rs = pst.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getTimestamp("transaction_date"), rs.getString("transaction_type"), rs.getDouble("amount"), rs.getDouble("new_balance")});
            }
        } catch (SQLException e) { }
    }

    private JButton createStyledButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false); b.setBackground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardFrame("Bhaskar", "1234567890", 14500.00).setVisible(true));
    }
}