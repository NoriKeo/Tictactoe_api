package requesthandlers;

import ControllerandConnection.ConnectionHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreatAccountHandler implements HttpHandler {
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
                    String hashedPassword = RequestUtil.hashPassword(password);
                    String hashedSecurityAnswer = RequestUtil.hashPassword(securityAnswer);

                    PreparedStatement insertStmt = connection.prepareStatement(
                            "INSERT INTO accounts (player_name, passwort, security_question) VALUES (?, ?, ?)");
                    insertStmt.setString(1, playerName);
                    insertStmt.setString(2, hashedPassword);
                    insertStmt.setString(3, hashedSecurityAnswer);

                    int row = insertStmt.executeUpdate();
                    if (row > 0) {
                        RequestUtil.sendResponse(exchange, "Account erstellt <3");
                        System.out.println("Account erstellt <3");
                    } else {
                        RequestUtil.sendResponse(exchange, "Fehler beim Account erstellen </3");
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
            RequestUtil.sendResponse(exchange, "Fehler nur POST");
        }

    }
}
