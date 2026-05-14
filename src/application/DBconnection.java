package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {

    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "enrollment_db";
    private static final String USER     = "root";
    private static final String PASSWORD = "";
    private static final String URL =
            "jdbc:mariadb://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useUnicode=true&characterEncoding=UTF-8"
            + "&serverTimezone=Asia/Manila";

    private static DBconnection instance;
    private Connection connection;

    private DBconnection() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB_Connection] Connected to enrollment_db.");
        } catch (ClassNotFoundException e) {
            System.err.println("[DB_Connection] Driver not found: " + e.getMessage());
            throw new RuntimeException("JDBC driver missing.", e);
        } catch (SQLException e) {
            System.err.println("[DB_Connection] Connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection failed.", e);
        }
    }

    public static DBconnection getInstance() {
        if (instance == null) {
            instance = new DBconnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("[DB_Connection] Reconnecting...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("[DB_Connection] Reconnect failed: " + e.getMessage());
            throw new RuntimeException("Could not reconnect to database.", e);
        }
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB_Connection] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB_Connection] Error closing: " + e.getMessage());
        }
    }
}