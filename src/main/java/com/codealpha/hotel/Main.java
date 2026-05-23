package com.codealpha.hotel;

import com.codealpha.hotel.config.DatabaseConfig;
import com.codealpha.hotel.ui.ConsoleUI;

/**
 * ============================================================
 *  Grand Horizon Hotel — Reservation Management System
 *  CodeAlpha Java Internship — Task 4
 *  Author  : Niha Wahid
 *  Version : 1.0
 *
 *  Tech Stack : Java 11 + MySQL + JDBC + DAO Pattern
 * ============================================================
 */
public class Main {

    public static void main(String[] args) {
        // Test DB connection on startup
        try {
            DatabaseConfig.getInstance().getConnection();
            System.out.println("\u001B[32m  ✅ Database connected successfully!\u001B[0m");
        } catch (Exception e) {
            System.out.println("\u001B[31m  ❌ Database connection failed: " + e.getMessage() + "\u001B[0m");
            System.out.println("\u001B[33m  ⚠️  Please check database.properties and ensure MySQL is running.\u001B[0m");
            return;
        }

        // Start the console UI
        ConsoleUI ui = new ConsoleUI();
        ui.start();

        // Clean up on exit
        DatabaseConfig.getInstance().closeConnection();
    }
}
