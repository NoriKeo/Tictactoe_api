package requesthandlers;

import database.ConnectionHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class CreatAccountHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            String request = new String(exchange.getRequestBody().readAllBytes()).trim();

            JsonNode jsonNode = RequestUtil.objectMapper.readTree(request);
            String playerName = jsonNode.get("playerName").asText();
            String password = jsonNode.get("password").asText();
            String securityAnswer = jsonNode.get("securityAnswer").asText();

            int row = createAccount(playerName, password, securityAnswer);
            int playerId;
            try {
                 playerId = getPlayerId(playerName, password, securityAnswer);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (row > 0) {

                RequestUtil.sendResponse(exchange, "Account erstellt <3" + playerId);
                System.out.println("Account erstellt <3");
            } else {
                RequestUtil.sendResponse(exchange, "Fehler beim Account erstellen </3");
            }

        } else {
            RequestUtil.sendResponse(exchange, "Fehler nur POST");
        }

    }

    public int createAccount(String playerName , String password, String securityAnswer) {

        int row;
        try (Connection connection = ConnectionHandler.getConnection()) {
            String hashedPassword = RequestUtil.hashPassword(password);
            String hashedSecurityAnswer = RequestUtil.hashPassword(securityAnswer);

            PreparedStatement insertStmt = connection.prepareStatement(
                    "INSERT INTO accounts (player_name, passwort, security_question) VALUES (?, ?, ?)");
            insertStmt.setString(1, playerName);
            insertStmt.setString(2, hashedPassword);
            insertStmt.setString(3, hashedSecurityAnswer);
            row = insertStmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return row;
    }
    
    public int getPlayerId(String playerName,String password,String securityAnswer) throws SQLException {
        int playerId = 0;
        String sql = "SELECT player_id FROM move WHERE player_name = ? AND passwort = ? AND security_answer = ? ";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setString(1, playerName);
            insertStmt.setString(2, password);
            insertStmt.setString(3, securityAnswer);

            try (ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    playerId = resultSet.getInt("player_id");

                }

            }
        }
        
        return playerId;
    }
}

