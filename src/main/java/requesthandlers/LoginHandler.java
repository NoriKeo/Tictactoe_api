package requesthandlers;

import database.ConnectionHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {


        if (!"POST".equals(exchange.getRequestMethod())) {
            RequestUtil.sendResponse(exchange, "Nur POST-Anfragen sind erlaubt!", 405);
            return;
        }

        String request = new String(exchange.getRequestBody().readAllBytes()).trim();

        try {
            JsonNode jsonNode = RequestUtil.objectMapper.readTree(request);
            String playerName = jsonNode.get("playerName").asText();
            String password = jsonNode.get("password").asText();
            Connection connection;
            try {
                 connection = ConnectionHandler.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            int playerId = login(playerName, password,connection);
            if (playerId >= 0) {
                RequestUtil.sendResponse(exchange, "Login erfolgreich! Player ID: " + playerId);

            } else {
                RequestUtil.sendResponse(exchange, "Login nicht erfolgreich!");
            }


        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private int login(String playerName, String password,Connection connection) throws SQLException {
        int playerId = 0;
        String sql = "SELECT player_id FROM accounts WHERE player_name = ? AND passwort = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.setString(2, RequestUtil.hashPassword(password));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                playerId = rs.getInt("player_id");
                System.out.println("Login erfolgreich! Player ID: " + playerId);
                return playerId;
            } else {
                return -1;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }
}
