package com.atm.dao;

import com.atm.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // Method to record a new transaction
    public boolean recordTransaction(String accountNumber, String type, double amount, double newBalance) {
        String query = "INSERT INTO transactions (account_number, transaction_type, amount, new_balance, transaction_date) VALUES (?, ?, ?, ?, NOW())";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {
            
            pst.setString(1, accountNumber);
            pst.setString(2, type);
            pst.setDouble(3, amount);
            pst.setDouble(4, newBalance);
            
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Double> getBalanceTrend(String accountNumber) {
    List<Double> balances = new ArrayList<>();
    // We get the last 7 transactions to plot the points
    String query = "SELECT new_balance FROM transactions WHERE account_number = ? " +
                   "ORDER BY transaction_date DESC LIMIT 7";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(query)) {
        pst.setString(1, accountNumber);
        ResultSet rs = pst.executeQuery();
        while (rs.next()) {
            balances.add(rs.getDouble("new_balance"));
        }
        // Reverse them so the oldest is on the left and newest is on the right
        java.util.Collections.reverse(balances);
    } catch (SQLException e) { e.printStackTrace(); }
    return balances;
}
    // Method to fetch the last 5 transactions for the Dashboard table
    public ResultSet getRecentTransactions(String accountNumber) {
        String query = "SELECT transaction_date, transaction_type, amount, new_balance " +
                       "FROM transactions WHERE account_number = ? " +
                       "ORDER BY transaction_date DESC LIMIT 5";
        
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, accountNumber);
            return pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public double getTodayWithdrawalTotal(String accountNumber) {
    double total = 0.0;
    // Query sums up all 'Withdraw' amounts for the current account for TODAY only
    String query = "SELECT SUM(amount) as today_spent FROM transactions " +
                   "WHERE account_number = ? AND transaction_type = 'Withdraw' " +
                   "AND DATE(transaction_date) = CURDATE()";
    
    try (Connection conn = com.atm.util.DBConnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(query)) {
        
        pst.setString(1, accountNumber);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            total = rs.getDouble("today_spent");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return total;
}
    
    public java.util.List<Object[]> getFullTransactionHistory(String accountNumber) {
    java.util.List<Object[]> history = new java.util.ArrayList<>();
    String query = "SELECT transaction_date, transaction_type, amount, new_balance " +
                   "FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";

    try (Connection conn = com.atm.util.DBConnection.getConnection();
         PreparedStatement pst = conn.prepareStatement(query)) {
        
        pst.setString(1, accountNumber);
        ResultSet rs = pst.executeQuery();
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        while (rs.next()) {
            Object[] row = {
                sdf.format(rs.getTimestamp("transaction_date")), // Column 0: String
                rs.getString("transaction_type"),               // Column 1: String
                rs.getDouble("amount"),                         // Column 2: Double (FIXED)
                rs.getDouble("new_balance")                     // Column 3: Double (FIXED)
            };
            history.add(row);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return history;
}
}