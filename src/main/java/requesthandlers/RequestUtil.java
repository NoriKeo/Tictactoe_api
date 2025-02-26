package requesthandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import database.ConnectionHandler;
import controller.ServerController;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class RequestUtil {

    public static String name;
    static String password;
    static String securityAnswer;
    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException, SQLException {
        initializeDatabase();
        ServerController serverController = new ServerController();
        serverController.serverStart(8000);
        System.out.println("Server läuft ");


    }


    public static void sendResponse(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, response, 200);
    }

    public static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static void sendInvalidMethodResponse(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "Invalid request Method.", 405);
    }

    public static void sendInvalidParameterResponse(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "Invalid request Parameters.", 400);
    }



    public static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void initializeDatabase() throws SQLException {
        try (Connection connection = ConnectionHandler.getConnection()) {
            Statement stmt = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS accounts (" +
                    "player_id SERIAL PRIMARY KEY, " +
                    "player_name varchar(255), " +
                    "passwort varchar(255), " +
                    "security_question varchar(255)" +
                    ");";
            stmt.execute(createTableSQL);

            System.out.println("Datenbank initialisiert.");
        }
    }


}
