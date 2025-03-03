package player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ControllerandConnection.ServerController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import login.Playername;
import readAndWrite.MatchHistoryRead;

public class MatchServer {

    public static List<Integer> playerPlaysList;
    public static List<Integer> computerPlaysList;

    public static void main(String[] args) throws IOException {

        ServerController serverController = new ServerController();
        serverController.serverStart(8000);
        System.out.println("Server läuft ");
        serverController.endpoints();

    }

    public static class MatchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                MatchHistorysplit matchHistorysplit = new MatchHistorysplit();

                Playername.playerId = 2;
                MatchHistoryRead.getInstance();
                try {
                    MatchHistoryRead.read();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                matchHistorysplit.split();

                Map<String, Object> data = new HashMap<>();
                data.put("playerId", Playername.playerId);
                data.put("playerPlays", matchHistorysplit.playersPlaces);
                data.put("computerPlays", matchHistorysplit.computersPlaces);

                // hab hier zur vereinfachung die win und draw variablen aus wintry gelöscht
                data.put("Winplayer", false);
                data.put("WinnerComputer", false);
                data.put("Draw", true);

                String response = new Gson().toJson(data);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    public static class Matchwriter implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                BufferedReader reader =
                    new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
                String body = reader.lines().collect(Collectors.joining());
                reader.close();

                JsonObject jsonObject = new JsonParser().parse(body).getAsJsonObject();
                playerPlaysList = new Gson().fromJson(jsonObject.getAsJsonArray("playerPlays"), List.class);
                computerPlaysList = new Gson().fromJson(jsonObject.getAsJsonArray("computerPlays"), List.class);

                System.out.println(playerPlaysList + "<p c> " + computerPlaysList);
                String response = new Gson().toJson("hallo");
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }

    }
}
