package requesthandlers;

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
        if (!"POST".equals(exchange.getRequestMethod())) {
            RequestUtil.sendResponse(exchange, "Nur POST-Anfragen sind erlaubt!", 405);
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        Connection connection;
        try {
            connection = ConnectionHandler.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONObject json = new JSONObject(requestBody.toString());
            int inputPlayerId = json.getInt("playerId");


            MatchReader matchReader = new MatchReader();
            int matchid = matchReader.matchStatus(inputPlayerId, 4, connection);

            if (matchid < 0) {
                RequestUtil.sendResponse(exchange, "Es gibt keine Matches unter der der PlayerId.", 400);
                return;
            }
            List<Integer> matches = machtHistorydata(inputPlayerId);
            List<Integer> playerMoves = matchHistorydataPlayer(matchid);
            List<Integer> ComputerMoves = matchHistorydataComputer(matchid);



        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public List<Integer> machtHistorydata(int playerId) {

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

    public List<Integer> matchHistorydataPlayer( int matchId) {
        String moveQuery = "SELECT  position, is_player FROM move WHERE match_id = ?";
        List<Integer> playerData = new ArrayList<>();

        try(Connection connection = ConnectionHandler.getConnection()) {
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

        try(Connection connection = ConnectionHandler.getConnection()) {
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







