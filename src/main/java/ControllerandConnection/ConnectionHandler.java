package ControllerandConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHandler {

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
}
