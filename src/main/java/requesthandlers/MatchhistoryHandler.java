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
import java.sql.Connection;
import java.sql.SQLException;

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
            int matchid = matchReader.matchStatus(inputPlayerId, 4,connection);

            if (matchid == -1) {
                RequestUtil.sendResponse(exchange, "Es gibt keine Matches unter der der PlayerId.", 400);
                return;
            }



        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
