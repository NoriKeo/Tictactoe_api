package requesthandlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.ConnectionHandler;
import database.MatchReader;
import database.Score;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

public class ScoreHandler implements HttpHandler {

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

        try {
            JSONObject json = new JSONObject(requestBody.toString());
            int inputPlayerId = json.getInt("playerId");

            System.out.println("Eingehende Anfrage -> playerId: " + inputPlayerId);
            Connection connection = ConnectionHandler.getConnection();

            MatchReader matchReader = new MatchReader();
            int matchid = matchReader.matchStatus(inputPlayerId, 4,connection);

            if (matchid == -1) {
                RequestUtil.sendResponse(exchange, "für die playerid " + inputPlayerId + "ligt kein score vor ", 400);
                return;
            } else {
                int playerScore;
                int computerScore;
                int drawScore;
                int[] score = Score.getInstance().readScore(inputPlayerId,connection);
                playerScore = score[0];
                computerScore = score[1];
                drawScore = score[2];
                RequestUtil.sendResponse(exchange, "playerscore " + playerScore + "computerScore" + computerScore + "drawScore" + drawScore, 200);
            }


        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}