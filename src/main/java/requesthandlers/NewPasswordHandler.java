package requesthandlers;

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
        if ("POST".equals(exchange.getRequestMethod())) {
            String request = new String(exchange.getRequestBody().readAllBytes()).trim();

            try {
                JsonNode jsonNode = RequestUtil.objectMapper.readTree(request);
                String playerName = jsonNode.get("playerName").asText();
                String password = jsonNode.get("password").asText();
                String securityAnswer = jsonNode.get("securityAnswer").asText();

                try (Connection connection = ConnectionHandler.getConnection()) {
                    PreparedStatement stmt = connection.prepareStatement(
                            "SELECT player_id FROM accounts WHERE player_name = ? AND security_question = ?");
                    stmt.setString(1, playerName);
                    stmt.setString(2, RequestUtil.hashPassword(securityAnswer));

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        int playerId = rs.getInt("player_id");
                        PreparedStatement insertStmt = connection.prepareStatement(
                                "UPDATE accounts SET passwort = ? WHERE player_id = ?");
                        insertStmt.setString(1, RequestUtil.hashPassword(password));
                        insertStmt.setInt(2, playerId);
                        insertStmt.executeUpdate();
                        RequestUtil.sendResponse(exchange, "Password erfolgreich geändert");
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
            RequestUtil.sendResponse(exchange, "Fehler nur POST");
        }
    }
}
