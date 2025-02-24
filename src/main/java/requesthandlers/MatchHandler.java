package requesthandlers;

import Board.Board;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import gamesInfo.Position;
import player.Player;
import readAndWrite.MatchReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.SQLException;

import static requesthandlers.RequestUtil.sendResponse;

public class MatchHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!"POST".equals(exchange.getRequestMethod())) {
            String response = "Nur POST-Anfragen sind erlaubt!";
            exchange.sendResponseHeaders(405, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }
        String inputplayerid = new String(exchange.getRequestBody().readAllBytes()).trim();
        String response = requestBody.toString().trim();


        MatchReader matchReader = new MatchReader();
        int playerId;

            playerId = matchReader.matchStartus(Integer.parseInt(response),4);
            System.out.println(playerId + "work");

        if (playerId == -1) {
            response = "Bitte eine zahl eingeben ";
            Board board = new Board();
            Player player = new Player();
            String input = requestBody.toString().trim();
            if (player.freefield(board, Integer.parseInt(input))) {
                System.out.println("Eingabe erhalten: " + input);
                Position position = null;
                response = "Eingabe akzeptiert: " + input + ". Gebe eine neue Zahl ein.";
                position = new Position(Integer.parseInt(input));
                board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');


            } else{
                response = "Ungültige Eingabe: " + input + ". Bitte gib eine Zahl zwischen 1 und 9 ein.";

            }


        } else {
            System.out.println("Problem");

        }

        // Sende die Antwort
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

}
