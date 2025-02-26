package requesthandlers;

import Board.Board;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import game.GamePlayMove;
import gamesInfo.Position;
import org.json.JSONException;
import org.json.JSONObject;
import player.Computer;
import player.Player;
import readAndWrite.MatchReader;
import readAndWrite.MatchWrite;
import readAndWrite.MoveReader;
import readAndWrite.MoveWriter;
import win.WinCheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;


public class MatchHandler implements HttpHandler {
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
            int move = json.getInt("move");

            System.out.println("Eingehende Anfrage -> playerId: " + inputPlayerId + ", move: " + move);

            MatchReader matchReader = new MatchReader();
            int matchid = matchReader.matchStatus(inputPlayerId, 4);

            if (matchid == -1) {
                newMatch(exchange, inputPlayerId, move);
            } else {
                handleExistingMatch(exchange, inputPlayerId, matchid, move);
            }


        } catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void newMatch(HttpExchange exchange, int inputPlayerId, int move) throws IOException {
        System.out.println("Match-ID nicht gefunden. Neues Spielbrett wird erstellt...");
        Board board = new Board();
        Player player = new Player();

        int matchidnew = MatchWrite.getInstance().createMatch(inputPlayerId);

        if (!player.freefield(board, move)) {
            RequestUtil.sendResponse(exchange, "Ungültige Eingabe: " + move + ". Bitte gib eine Zahl zwischen 1 und 9 ein.", 400);
            return;
        }



            MoveWriter moveWriter = new MoveWriter();
            Position position = new Position(move);
            board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');
            moveWriter.newPlayerMove(matchidnew, move);



            Position computerPosition = getComputerMove(board,inputPlayerId,matchidnew);
            String computerMove = String.valueOf(computerPosition.getRow() + computerPosition.getColumn());
            if (computerPosition != null) {
                board.getRows().get(computerPosition.getRow()).getFields().get(computerPosition.getColumn()).setGameCharacter('¤');
                moveWriter.newComputerMove(matchidnew, Integer.parseInt(computerMove));
            } else {
                System.out.println("Computer movement nicht gefunden");
            }
            RequestUtil.sendResponse(exchange, "Neue Match-ID erstellt! Eingabe akzeptiert: " + move + ". Computer antwortet mit: " + computerMove + ". Gebe eine neue Zahl ein.", 200);



    }

    public void handleExistingMatch(HttpExchange exchange, int inputPlayerId, int matchid, int move) throws IOException {
        System.out.println("Match-ID erfolgreich gefunden: " + matchid);
        RequestUtil.sendResponse(exchange, "Match-ID erfolgreich gesetzt: " + matchid + ". Du kannst weiterspielen.", 200);

        Board board = getBoard(exchange,matchid);
        Player player = new Player();

        if (player.freefield(board, move)) {
            MoveWriter moveWriter = new MoveWriter();
            Position position = new Position(move);
            board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');
            moveWriter.newPlayerMove(matchid, move);
            GamePlayMove winMove = new GamePlayMove(position, '♡');
            if (WinCheck.isWin(board, winMove)) {
                MatchWrite.getInstance().endMatch(matchid, inputPlayerId, 1);
                RequestUtil.sendResponse(exchange, "Spiel beendet Gewinner bist du Starte ein neues Spiel um weiterzuspielen", 200);
            }

            Position computerPosition = getComputerMove(board,inputPlayerId,matchid);
            String computerMove = String.valueOf(computerPosition.getRow() + computerPosition.getColumn());
            if (computerPosition != null) {
                board.getRows().get(computerPosition.getRow()).getFields().get(computerPosition.getColumn()).setGameCharacter('¤');
                moveWriter.newComputerMove(matchid, Integer.parseInt(computerMove));

                if (Computer.winsStrategy(board).isEmpty()) {
                    MatchWrite.getInstance().endMatch(matchid, inputPlayerId, 3);
                    RequestUtil.sendResponse(exchange, "Spiel beendet  Starte ein neues Spiel um weiterzuspielen", 200);

                }
                if (WinCheck.isWin(board, winMove)) {
                    MatchWrite.getInstance().endMatch(matchid, inputPlayerId, 6);
                    RequestUtil.sendResponse(exchange, "Spiel beendet Gewinner ist der Computer Starte ein neues Spiel um weiterzuspielen", 200);

                }

            } else {
                System.out.println("Computer movement nicht gefunden");
            }
            RequestUtil.sendResponse(exchange, " Eingabe akzeptiert: " + move + ". Computer antwortet mit: " + computerMove + ". Gebe eine neue Zahl ein.", 200);

        }


    }

    private Board getBoard(HttpExchange exchange, int matchid) throws IOException {
        Board board = new Board();
        int[] playerPosition = MoveReader.getInstance().getMoves(matchid, true);
        if (playerPosition != null) {
            for (int x : playerPosition) {
                Position position = new Position(x);
                board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');
            }
        }
        int[] computerPlays = MoveReader.getInstance().getMoves(matchid, false);
        if (computerPlays != null) {
            for (int x : computerPlays) {
                Position position = new Position(x);
                board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('¤');
                RequestUtil.sendResponse(exchange, "Dies Felder sind vom Computer besetzt " + Arrays.toString(computerPlays) + "und dies von dir " + Arrays.toString(playerPosition) + "setze eine neue zahl", 200);

            }
        }
        return board;
    }

    private Position getComputerMove(Board board, int playerId, int matchId) {
        int matchCounter = MatchReader.getInstance().matchCounter(playerId);
        int moveCounter = MoveReader.getInstance().moveCounter(playerId, matchId);
        return Computer.getComputerMovement(board, matchCounter, moveCounter);
    }


}


