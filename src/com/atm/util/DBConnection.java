package com.atm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/mini_atm_db";
    private static final String USER = "root"; 
    private static final String PASSWORD = "Bhaskar123"; 

    private static Connection connection = null;

    // Method to get the connection
    public static Connection getConnection() {
        try {
            // Check if connection is null or closed
            if (connection == null || connection.isClosed()) {
                // Register MySQL Driver (Optional in newer JDBC, but good practice)
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database Connected Successfully!");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Connection Failed! Check output console.");
            e.printStackTrace();
        }
        return connection;
    }
}