package requesthandlers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.ConnectionHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.LiquibaseMigrationService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (!"POST".equals(exchange.getRequestMethod())) {
            RequestUtil.sendResponse(exchange, "Nur POST-Anfragen sind erlaubt!", 405);
            return;
        }

        String request = new String(exchange.getRequestBody().readAllBytes()).trim();

        try {
            JsonNode jsonNode = RequestUtil.objectMapper.readTree(request);
            String playerName = jsonNode.get("playerName").asText();
            String password = jsonNode.get("password").asText();

            int playerId = login(playerName, password, ConnectionHandler.getConnection());
            if (playerId >= 0) {

                ObjectNode responseJson = RequestUtil.objectMapper.createObjectNode();
                responseJson.put("message", "Login erfolgreich!");
                responseJson.put("playerId", playerId);
                RequestUtil.sendResponse(exchange, responseJson.toString());


                //RequestUtil.sendResponse(exchange, "Login erfolgreich! Player ID: " + playerId);

            } else {
                ObjectNode responseJson = RequestUtil.objectMapper.createObjectNode();
                responseJson.put("message", "Login nicht erfolgreich!");

                RequestUtil.sendResponse(exchange, responseJson.toString());
                //RequestUtil.sendResponse(exchange, "Login nicht erfolgreich!");
            }


        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }


    }

    private int login(String playerName, String password,Connection connection) throws SQLException {
        int playerId;
        String sql = "SELECT player_id FROM accounts WHERE player_name = ? AND passwort = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            stmt.setString(2, RequestUtil.hashPassword(password));
            //stmt.setString(2, password);


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
