package player;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import login.Playername;
import readAndWrite.MatchHistoryRead;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MatchServer {



        public static void main(String[] args) throws IOException {
            int serverPort = 8000 ;

            HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);


                server.createContext("/api/hello", new MatchHandler());



            server.setExecutor(null);
            System.out.println("Server läuft " + serverPort);
            server.start();
        }

        static class MatchHandler implements HttpHandler {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                if ("GET".equals(exchange.getRequestMethod())) {


                    //String playerId = "1";

                    Playername.playerId = 2;
                    MatchHistoryRead.getInstance();
                    try {
                        MatchHistoryRead.read();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                    MatchHistorysplit.split();

                    Map<String, Object> data = new HashMap<>();
                    data.put("playerId", Playername.playerId);
                    data.put("playerPlays", MatchHistorysplit.playersPlaces);
                    data.put("computerPlays", MatchHistorysplit.computersPlaces);
                    data.put("Win",true);


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


}
