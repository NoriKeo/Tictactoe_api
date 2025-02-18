package player;

import ControllerandConnection.ConnectionHandler;
import ControllerandConnection.ServerController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import login.Playername;
import readAndWrite.MatchHistoryRead;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class MatchServer {

   public static List<Integer> playerPlaysList ;
    static  List<Integer> computerPlaysList ;

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


                    //String playerId = "1";

                    Playername.playerId = 2;
                    MatchHistoryRead.getInstance();
                    try {
                        MatchHistoryRead.read();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    matchHistorysplit.split();
                    System.out.println("help 1 ");

                    Map<String, Object> data = new HashMap<>();
                    data.put("playerId", Playername.playerId);
                    data.put("playerPlays", matchHistorysplit.playersPlaces);
                    data.put("computerPlays", matchHistorysplit.computersPlaces);
                    data.put("Win",true);

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

                    Set<Integer> allFields = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

                    Set<Integer> occupiedFields = new HashSet<>();
                    occupiedFields.addAll(playerPlaysList);
                    occupiedFields.addAll(computerPlaysList);
                    allFields.removeAll(occupiedFields);
                    Map<String, Object> data = new HashMap<>();
                    data.put("freeFields", allFields);
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
