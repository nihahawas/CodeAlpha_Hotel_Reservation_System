package com.codealpha.hotel.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DatabaseConfig — Singleton pattern for managing MySQL connection.
 * Reads credentials from database.properties file.
 */
public class DatabaseConfig {

    private static DatabaseConfig instance;
    private Connection connection;

    private String url;
    private String username;
    private String password;
    private String driver;

    // ─── Private Constructor ───────────────────────────────────────────────────
    private DatabaseConfig() {
        loadProperties();
    }

    // ─── Singleton Instance ────────────────────────────────────────────────────
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            synchronized (DatabaseConfig.class) {
                if (instance == null) {
                    instance = new DatabaseConfig();
                }
            }
        }
        return instance;
    }

    // ─── Load Properties ──────────────────────────────────────────────────────
    private void loadProperties() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (is == null) {
                throw new RuntimeException("database.properties not found in classpath!");
            }
            props.load(is);
            this.url      = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
            this.driver   = props.getProperty("db.driver");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database.properties: " + e.getMessage());
        }
    }

    // ─── Get Connection ────────────────────────────────────────────────────────
    public Connection getConnection() throws SQLException {
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found: " + e.getMessage());
        }

        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    // ─── Close Connection ─────────────────────────────────────────────────────
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
