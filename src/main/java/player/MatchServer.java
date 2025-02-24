/*
package player;

import ControllerandConnection.ServerController;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import requesthandlers.RequestUtil;
import readAndWrite.MatchReader;
import win.Wintry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class MatchServer {

   public static List<Integer> playerPlaysList ;
    public static  List<Integer> computerPlaysList ;

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



                    RequestUtil.playerId = 2;
                    MatchReader.getInstance();
                    try {
                        MatchReader.read();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    matchHistorysplit.split();
                    System.out.println("help 1 ");

                    Map<String, Object> data = new HashMap<>();
                    data.put("playerId", RequestUtil.playerId);
                    data.put("playerPlays", matchHistorysplit.playersPlaces);
                    data.put("computerPlays", matchHistorysplit.computersPlaces);
                    data.put("Winplayer", Wintry.playerWin);
                    data.put("WinnerComputer", Wintry.computerWin);
                    data.put("Draw", Wintry.draw);


                    System.out.println("help 2 ");

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
                    BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));
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
                }else {
                    exchange.sendResponseHeaders(405, -1);
                }
            }

        }
}
*/
