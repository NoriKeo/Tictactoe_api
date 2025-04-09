package requesthandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import database.ConnectionHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NewPasswordHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
            exchange.sendResponseHeaders(204, -1);
            return;

        }

        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            RequestUtil.sendResponse(exchange, "Nur POST-Anfragen sind erlaubt!", 405);
            return;
        }

        String request = new String(exchange.getRequestBody().readAllBytes()).trim();
        try {
            JsonNode jsonNode = RequestUtil.objectMapper.readTree(request);

            String playerName = jsonNode.get("playerName").asText();
            String password = jsonNode.get("password").asText();
            String securityAnswer = jsonNode.get("securityAnswer").asText();

            if (playerName.isEmpty() || password.isEmpty() || securityAnswer.isEmpty()) {
                RequestUtil.sendResponse(exchange, "Alle Felder müssen ausgefüllt werden!", 400);
                return;
            }

            handlePasswordReset(exchange, playerName, password, securityAnswer);

        } catch (IOException e) {
            System.err.println("Fehler beim Verarbeiten der Anfrage: " + e.getMessage());
            RequestUtil.sendResponse(exchange, "Ungültige Anfrage.", 400);
        }
    }

    private void handlePasswordReset(HttpExchange exchange, String playerName, String password, String securityAnswer)  {
        final String query = "SELECT player_id FROM accounts WHERE player_name = ? AND security_question = ?";

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, playerName);
            stmt.setString(2, RequestUtil.hashPassword(securityAnswer));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int playerId = rs.getInt("player_id");

                    updatePassword(connection, playerId, password);

                    ObjectNode responseJson = RequestUtil.objectMapper.createObjectNode();
                    responseJson.put("message", "Login works!");
                    responseJson.put("playerId", playerId);
                    RequestUtil.sendResponse(exchange, responseJson.toString());
                } else {
                    RequestUtil.sendResponse(exchange, "Answer or name incorrect!", 400);
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            try {
                RequestUtil.sendResponse(exchange, "database problem", 500);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void updatePassword(Connection connection, int playerId, String password)  {
        final String updateQuery = "UPDATE accounts SET passwort = ? WHERE player_id = ?";

        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
            updateStmt.setString(1, RequestUtil.hashPassword(password));
            updateStmt.setInt(2, playerId);
            updateStmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Fehler beim Verarbeiten der Anfrage: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
