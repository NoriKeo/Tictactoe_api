package requesthandlers;

import Board.Board;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import gamesInfo.Position;
import org.json.JSONObject;
import player.Computer;
import player.Player;
import readAndWrite.MatchReader;
import readAndWrite.MatchWrite;
import readAndWrite.MoveReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;


public class MatchHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            RequestUtil.sendResponse(exchange, "Nur POST-Anfragen sind erlaubt!", 405);
            return;
        }

        // Lese den Request-Body als JSON
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBody.append(line);
        }

        try {
            // JSON parsen
            JSONObject json = new JSONObject(requestBody.toString());
            int inputPlayerId = json.getInt("playerId");
            int move = json.getInt("move");

            System.out.println("Eingehende Anfrage -> playerId: " + inputPlayerId + ", move: " + move);

            MatchReader matchReader = new MatchReader();
            int playerId = matchReader.matchStatus(inputPlayerId, 4);

            if (playerId == -1) {
                System.out.println("Match-ID nicht gefunden. Neues Spielbrett wird erstellt...");
                Board board = new Board();
                Player player = new Player();
                int matchid = MatchWrite.getInstance().createMatch(inputPlayerId);

                if (player.freefield(board, move)) {
                    Position position = new Position(move);
                    board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');

                    int matchCounter = MatchReader.getInstance().matchCounter(inputPlayerId);
                    int moveCounter = MoveReader.getInstance().moveCounter(inputPlayerId, matchid);
                    Position computerPosition = Computer.getComputerMovement(board, matchCounter, moveCounter);
                    if (computerPosition != null) {
                        board.getRows().get(computerPosition.getRow()).getFields().get(computerPosition.getColumn()).setGameCharacter('¤');

                    }else {
                        System.out.println("Computer movement nicht gefunden");
                    }
                    RequestUtil.sendResponse(exchange, "Neue Match-ID erstellt! Eingabe akzeptiert: " + move + ". Computer antwortet mit: " + computerPosition.toString() + ". Gebe eine neue Zahl ein.", 200);

                } else {
                    RequestUtil.sendResponse(exchange, "Ungültige Eingabe: " + move + ". Bitte gib eine Zahl zwischen 1 und 9 ein.", 400);
                }
            } else {
                System.out.println("Match-ID erfolgreich gefunden: " + playerId);
                RequestUtil.sendResponse(exchange, "Match-ID erfolgreich gesetzt: " + playerId + ". Du kannst weiterspielen.", 200);
            }
        } catch (Exception e) {
            RequestUtil.sendResponse(exchange, "Ungültige Eingabe! Bitte JSON mit 'playerId' und 'move' senden.", 400);
        }
    }


}


