package requesthandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.ConnectionHandler;
import database.MatchReader;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchhistoryHandler implements HttpHandler {
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

        System.out.println("Empfangene POST-Anfrage");

        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        try (Connection connection = ConnectionHandler.getConnection()) {
            JSONObject json = new JSONObject(requestBody.toString());
            if (!json.has("playerId")) {
                RequestUtil.sendResponse(exchange, "Fehlender Parameter: playerId", 400);
                return;
            }

            int inputPlayerId = json.getInt("playerId");
            MatchReader matchReader = new MatchReader();
            int matchId = matchReader.matchStatus(inputPlayerId, 1, connection);
            if (matchId == -1) {
                 matchId = matchReader.matchStatus(inputPlayerId, 2, connection);
            }
            if (matchId == -1) {
                matchId = matchReader.matchStatus(inputPlayerId, 3, connection);
            }
            if (matchId == -1) {
                matchId = matchReader.matchStatus(inputPlayerId, 4, connection);
            }
            if (matchId < 0) {
                RequestUtil.sendResponse(exchange, "Es gibt keine Matches unter dieser PlayerId.", 400);
                return;
            }

            List<Integer> matches = matchHistorydata(inputPlayerId);
            List<Integer> selectedMatches = matches.subList(0, Math.min(matches.size(), 10));

            List<List<Integer>> playerMovesList = new ArrayList<>();
            List<List<Integer>> computerMovesList = new ArrayList<>();

            for (int id : selectedMatches) {
                playerMovesList.add(matchHistorydataPlayer(id));
                computerMovesList.add(matchHistorydataComputer(id));
            }

            ObjectMapper objectMapper = RequestUtil.objectMapper;
            ObjectNode responseJson = objectMapper.createObjectNode();

            for (int i = 0; i < selectedMatches.size(); i++) {
                responseJson.set("playerMoves" + (i + 1), objectMapper.valueToTree(playerMovesList.get(i)));
                responseJson.set("computerMoves" + (i + 1), objectMapper.valueToTree(computerMovesList.get(i)));
                System.out.println("player "+ i +playerMovesList.get(i));
                System.out.println("computer "+ i +computerMovesList.get(i));
            }

            RequestUtil.sendResponse(exchange, responseJson.toString());
        } catch (SQLException e) {
            RequestUtil.sendResponse(exchange, "Datenbankfehler: " + e.getMessage(), 500);
        } catch (JSONException e) {
            RequestUtil.sendResponse(exchange, "Fehlerhafte JSON-Daten", 400);
        }
    }





    public List<Integer> matchHistorydata(int playerId) {

        String query = "SELECT id FROM match WHERE player_id = ?";
        List<Integer> matchIds = new ArrayList<>();

        try (Connection conn = ConnectionHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, playerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                matchIds.add(rs.getInt("id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matchIds;
    }

    public List<Integer> matchHistorydataPlayer(int matchId) {
        String moveQuery = "SELECT  position, is_player FROM move WHERE match_id = ?";
        List<Integer> playerData = new ArrayList<>();

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(moveQuery);
            stmt.setInt(1, matchId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                boolean is_player = rs.getBoolean("is_player");
                if (is_player == true) {
                    playerData.add(rs.getInt("position"));
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return playerData;
    }

    public List<Integer> matchHistorydataComputer(int matchId) {
        String moveQuery = "SELECT  position, is_player FROM move WHERE match_id = ?";
        List<Integer> computerData = new ArrayList<>();

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(moveQuery);
            stmt.setInt(1, matchId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                boolean is_player = rs.getBoolean("is_player");
                if (is_player == false) {
                    computerData.add(rs.getInt("position"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return computerData;
    }
}







