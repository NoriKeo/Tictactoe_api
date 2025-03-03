package login;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ControllerandConnection.ConnectionHandler;
import ControllerandConnection.ServerController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Playername {

    static final ObjectMapper objectMapper = new ObjectMapper();
    public static String name;
    public static int playerId;
    static String password;
    static String securityAnswer;

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

    public static class CreatAccountHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String request = new String(exchange.getRequestBody().readAllBytes()).trim();

                try {
                    JsonNode jsonNode = objectMapper.readTree(request);
                    String playerName = jsonNode.get("playerName").asText();
                    String password = jsonNode.get("password").asText();
                    String securityAnswer = jsonNode.get("securityAnswer").asText();

                    try (Connection connection = ConnectionHandler.getConnection()) {
                        String hashedPassword = hashPassword(password);
                        String hashedSecurityAnswer = hashPassword(securityAnswer);

                        PreparedStatement insertStmt = connection.prepareStatement(
                            "INSERT INTO accounts (player_name, passwort, security_question) VALUES (?, ?, ?)");
                        insertStmt.setString(1, playerName);
                        insertStmt.setString(2, hashedPassword);
                        insertStmt.setString(3, hashedSecurityAnswer);

                        int row = insertStmt.executeUpdate();
                        if (row > 0) {
                            sendResponse(exchange, "Account erstellt <3");
                            System.out.println("Account erstellt <3");
                        } else {
                            sendResponse(exchange, "Fehler beim Account erstellen </3");
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                sendResponse(exchange, "Fehler nur POST");
            }

        }
    }

    public static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String request = new String(exchange.getRequestBody().readAllBytes()).trim();

                try {
                    JsonNode jsonNode = objectMapper.readTree(request);
                    String playerName = jsonNode.get("playerName").asText();
                    String password = jsonNode.get("password").asText();

                    try (Connection connection = ConnectionHandler.getConnection()) {
                        PreparedStatement stmt = connection.prepareStatement(
                            "SELECT player_id FROM accounts WHERE player_name = ? AND passwort = ?");
                        stmt.setString(1, playerName);
                        stmt.setString(2, hashPassword(password));

                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            int playerId = rs.getInt("player_id");
                            sendResponse(exchange, "Login erfolgreich! Player ID: " + playerId);
                            System.out.println("Login erfolgreich! Player ID: " + playerId);
                        } else {
                            sendResponse(exchange, "Fehler beim Login");
                        }

                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            } else {
                sendResponse(exchange, "Fehler nur POST");
            }
        }
    }

    public static class newPasswordHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String request = new String(exchange.getRequestBody().readAllBytes()).trim();

                try {
                    JsonNode jsonNode = objectMapper.readTree(request);
                    String playerName = jsonNode.get("playerName").asText();
                    String password = jsonNode.get("password").asText();
                    String securityAnswer = jsonNode.get("securityAnswer").asText();

                    try (Connection connection = ConnectionHandler.getConnection()) {
                        PreparedStatement stmt = connection.prepareStatement(
                            "SELECT player_id FROM accounts WHERE player_name = ? AND security_question = ?");
                        stmt.setString(1, playerName);
                        stmt.setString(2, hashPassword(securityAnswer));

                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            int playerId = rs.getInt("player_id");
                            PreparedStatement insertStmt = connection.prepareStatement(
                                "UPDATE accounts SET passwort = ? WHERE player_id = ?");
                            insertStmt.setString(1, hashPassword(password));
                            insertStmt.setInt(2, playerId);
                            insertStmt.executeUpdate();
                            sendResponse(exchange, "Password erfolgreich geändert");
                            System.out.println("Passwort erfolgreich geändert ");
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }
            } else {
                sendResponse(exchange, "Fehler nur POST");
            }
        }
    }

    static class StartHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                try (Connection connection = ConnectionHandler.getConnection()) {

                    PreparedStatement checkCredentialsStmt = connection.prepareStatement(
                        "SELECT player_id FROM accounts WHERE player_name = ? AND passwort = ?");
                    checkCredentialsStmt.setString(1, name);
                    checkCredentialsStmt.setString(2, password);

                    ResultSet resultSet = checkCredentialsStmt.executeQuery();
                    if (resultSet.next()) {
                        playerId = resultSet.getInt("player_id");
                        sendResponse(exchange, "Erfolgreich eingeloggt! player.Player ID: " + playerId);
                        System.out.println("Erfolgreich eingeloggt! player.Player ID: " + playerId);
                    } else {
                        sendResponse(exchange, "Spielername oder Passwort falsch", 401);
                        System.out.println("Spielername oder Passwort falsch");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    sendResponse(exchange, "Datenbankfehler", 500);
                    System.out.println("Datenbankfehler");
                }
            } else {
                System.out.println("nix funktiert ");
            }
        }
    }
}
