package requesthandlers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import database.ConnectionHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.sql.*;

public class CreatAccountHandler implements HttpHandler {
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

            JsonNode jsonNode = RequestUtil.objectMapper.readTree(request);
            String playerName = jsonNode.get("playerName").asText();
            String password = jsonNode.get("password").asText();
            String securityAnswer = jsonNode.get("securityAnswer").asText();

        int playerId = 0;
        try {
            Connection connection = ConnectionHandler.getConnection();
            playerId = createAccount(playerName, password, securityAnswer, connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        if (playerId > 0) {

            ObjectNode responseJson = RequestUtil.objectMapper.createObjectNode();
            responseJson.put("message", "Account erstellt <3");
            responseJson.put("playerId", playerId);
            RequestUtil.sendResponse(exchange, responseJson.toString());
                //RequestUtil.sendResponse(exchange, "Account erstellt <3" + playerId);
                System.out.println("Account erstellt <3");
            } else {
            ObjectNode responseJson = RequestUtil.objectMapper.createObjectNode();
            responseJson.put("message", "Fehler beim Account erstellen </3");
            RequestUtil.sendResponse(exchange, responseJson.toString());

            //RequestUtil.sendResponse(exchange, "Fehler beim Account erstellen </3");
            }



    }

    public int createAccount(String playerName, String password, String securityAnswer,Connection connection) throws SQLException {
        String sql = "INSERT INTO accounts (player_name, passwort, security_question) VALUES (?,?,?)";
        int playerId = 0;
        if (playerName != null && !playerName.isEmpty() && password != null && !password.isEmpty() && securityAnswer != null && !securityAnswer.isEmpty()) {
        String hashedPassword = RequestUtil.hashPassword(password);
        String hashedSecurityAnswer = RequestUtil.hashPassword(securityAnswer);
        try (PreparedStatement insertStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, playerName);
            insertStmt.setString(2, hashedPassword);
            insertStmt.setString(3, hashedSecurityAnswer);
            insertStmt.executeUpdate();

            try(ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    playerId = generatedKeys.getInt(1);
                    return playerId;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
        }
        return playerId;

    }


}

