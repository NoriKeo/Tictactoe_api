package requesthandlers;

import ControllerandConnection.ConnectionHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if ("POST".equals(exchange.getRequestMethod())) {
            String request = new String(exchange.getRequestBody().readAllBytes()).trim();

            try {
                JsonNode jsonNode = RequestUtil.objectMapper.readTree(request);
                String playerName = jsonNode.get("playerName").asText();
                String password = jsonNode.get("password").asText();

                int playerId = login(playerName, password);
                if (playerId >= 0) {
                    RequestUtil.sendResponse(exchange, "Login erfolgreich! Player ID: " + playerId);

                } else {
                    RequestUtil.sendResponse(exchange, "Login nicht erfolgreich!");
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            RequestUtil.sendInvalidMethodResponse(exchange);
        }

    }

    private int login(String playerName, String password) {
        int playerId = 0;
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT player_id FROM accounts WHERE player_name = ? AND passwort = ?");
            stmt.setString(1, playerName);
            stmt.setString(2, RequestUtil.hashPassword(password));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                playerId = rs.getInt("player_id");
                System.out.println("Login erfolgreich! Player ID: " + playerId);
            } else {
                return -1;
            }

        } catch (SQLException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return playerId;

    }
}
