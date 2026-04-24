package com.atm.dao;

import com.atm.util.DBConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AccountDAO {

    public double getBalance(String accountNumber) {
        String query = "SELECT balance FROM accounts WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, accountNumber);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public boolean updateBalance(String accountNumber, double newBalance) {
        String query = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setDouble(1, newBalance);
            pst.setString(2, accountNumber);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // NEW: Fetch all data for the Summary Panel
    public Map<String, Object> getAccountSummary(String accountNumber) {
    Map<String, Object> summary = new java.util.HashMap<>();
    // Query updated to match your actual DB columns: customer_name, account_type, daily_limit
    String query = "SELECT customer_name, balance, account_type, daily_limit FROM accounts WHERE account_number = ?";
    
    try (java.sql.Connection conn = com.atm.util.DBConnection.getConnection();
         java.sql.PreparedStatement pst = conn.prepareStatement(query)) {
        
        pst.setString(1, accountNumber);
        java.sql.ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            summary.put("name", rs.getString("customer_name"));
            summary.put("balance", rs.getDouble("balance"));
            summary.put("type", rs.getString("account_type"));
            summary.put("limit", rs.getDouble("daily_limit"));
        }
    } catch (java.sql.SQLException e) {
        System.err.println("DAO Error: " + e.getMessage());
        e.printStackTrace();
    }
    return summary;
}
    public Map<String, Object> getAccountDetails(String accountNumber) {
        Map<String, Object> details = new HashMap<>();
        String query = "SELECT balance, account_type FROM accounts WHERE account_number = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, accountNumber);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                details.put("balance", rs.getDouble("balance"));
                details.put("type", rs.getString("account_type"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return details;
    }
    
    public boolean registerNewUser(String accNum, String name, String type, String pin, double limit) {
    // REMOVED 'pin' from this query
    String sqlAccount = "INSERT INTO accounts (account_number, customer_name, account_type, balance, daily_limit) "
                      + "VALUES (?, ?, ?, 0.0, ?)";
    
    // This query handles the PIN
    String sqlLogin = "INSERT INTO login (account_number, pin) VALUES (?, ?)";

    Connection conn = null;
    try {
        conn = com.atm.util.DBConnection.getConnection();
        conn.setAutoCommit(false); 

        // 1. Insert into Accounts Table (Now only 4 parameters)
        try (PreparedStatement pst1 = conn.prepareStatement(sqlAccount)) {
            pst1.setString(1, accNum);
            pst1.setString(2, name);
            pst1.setString(3, type);
            pst1.setDouble(4, limit); 
            pst1.executeUpdate();
        }

        // 2. Insert into Login Table
        try (PreparedStatement pst2 = conn.prepareStatement(sqlLogin)) {
            pst2.setString(1, accNum);
            pst2.setString(2, pin);
            pst2.executeUpdate();
        }

        conn.commit();
        return true;

    } catch (SQLException e) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        e.printStackTrace();
        return false;
    } finally {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
    public boolean performWithdrawal(String accountNumber, double amount, double newBalance) {
    String updateQuery = "UPDATE accounts SET balance = ? WHERE account_number = ?";
    String transQuery = "INSERT INTO transactions (account_number, transaction_type, amount, new_balance, transaction_date) VALUES (?, 'Withdraw', ?, ?, NOW())";
    
    Connection conn = null;
    try {
        conn = DBConnection.getConnection();
        conn.setAutoCommit(false); // Start Transaction: Both must succeed or both fail

        // 1. Update Balance
        try (PreparedStatement pstUpdate = conn.prepareStatement(updateQuery)) {
            pstUpdate.setDouble(1, newBalance);
            pstUpdate.setString(2, accountNumber);
            pstUpdate.executeUpdate();
        }

        // 2. Record Transaction History
        try (PreparedStatement pstTrans = conn.prepareStatement(transQuery)) {
            pstTrans.setString(1, accountNumber);
            pstTrans.setDouble(2, amount);
            pstTrans.setDouble(3, newBalance);
            pstTrans.executeUpdate();
        }

        conn.commit(); // Save changes to DB
        return true;
    } catch (SQLException e) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
        e.printStackTrace();
        return false;
    } finally {
        if (conn != null) {
            try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
    
    public java.util.List<String> getLastLogins(String accountNumber) {
    java.util.List<String> logins = new java.util.ArrayList<>();
    // This assumes you have a 'transaction_date' in your transactions table 
    // or a dedicated login_logs table. Using transactions as a fallback:
    String query = "SELECT transaction_date FROM transactions WHERE account_number = ? " +
                   "ORDER BY transaction_date DESC LIMIT 4";
    
    try (java.sql.Connection conn = com.atm.util.DBConnection.getConnection();
         java.sql.PreparedStatement pst = conn.prepareStatement(query)) {
        
        pst.setString(1, accountNumber);
        java.sql.ResultSet rs = pst.executeQuery();
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM, hh:mm a");
        
        while (rs.next()) {
            logins.add(sdf.format(rs.getTimestamp("transaction_date")));
        }
    } catch (java.sql.SQLException e) {
        e.printStackTrace();
    }
    return logins;
}
}