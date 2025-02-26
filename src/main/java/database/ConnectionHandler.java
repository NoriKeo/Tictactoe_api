package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHandler {

    public static DriverManager DatabaseConnection;
    static String jdbcUrl = "jdbc:postgresql://localhost:5432/tiktaktoe_database";
    static String username = "postgres";
    static String password = "mysecretpassword";

    private static Connection connection;


    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(jdbcUrl, username, password);
        }
        return connection;
    }

    /*public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }*/

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        ConnectionHandler.password = password;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        ConnectionHandler.username = username;
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static void setJdbcUrl(String jdbcUrl) {
        ConnectionHandler.jdbcUrl = jdbcUrl;
    }
}
