package requesthandlers;

import board.Board;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.*;
import win.GamePlayMove;
import board.Position;
import org.json.JSONException;
import org.json.JSONObject;
import game.Computer;
import game.Player;
import win.WinCheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
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
            if (!checkInput(exchange, move)) {
                return;
            }
            MatchReader matchReader = new MatchReader();
            int matchid = matchReader.matchStatus(inputPlayerId, 4,connection);

            if (matchid == -1) {
                newMatch(exchange, inputPlayerId, move);
            } else {
                handleExistingMatch(exchange, inputPlayerId, matchid, move);
            }


        } catch (JSONException e) {
            RequestUtil.sendInvalidParameterResponse(exchange);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    Connection connection;

    {
        try {
            connection = ConnectionHandler.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void newMatch(HttpExchange exchange, int inputPlayerId, int move) throws IOException {
        System.out.println("Match-ID nicht gefunden. Neues Spielbrett wird erstellt...");


        Board board = new Board();
        Player player = new Player();

        int matchidnew = 0;
        try {
            matchidnew = MatchWrite.getInstance().createMatch(inputPlayerId,connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (!player.freeField(board, move)) {
            RequestUtil.sendResponse(exchange, "Ungültige Eingabe: " + move + ". Bitte gib eine Zahl zwischen 1 und 9 ein.", 400);
            return;
        }


        MoveWriter moveWriter = new MoveWriter();
        Position position = new Position(move);
        board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');
        try {
            moveWriter.newPlayerMove(matchidnew, move,connection);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



        Position computerPosition = getComputerMove(board, inputPlayerId, matchidnew);
        String computerMove = String.valueOf(computerPosition.getRow() + computerPosition.getColumn());
        if (computerPosition != null) {
            board.getRows().get(computerPosition.getRow()).getFields().get(computerPosition.getColumn()).setGameCharacter('¤');
            try {
                moveWriter.newComputerMove(matchidnew, Integer.parseInt(computerMove),connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Computer movement nicht gefunden");
        }
        RequestUtil.sendResponse(exchange, "Neue Match-ID erstellt! Eingabe akzeptiert: " + move + ". Computer antwortet mit: " + computerMove + ". Gebe eine neue Zahl ein.", 200);


    }

    public void handleExistingMatch(HttpExchange exchange, int inputPlayerId, int matchid, int move) throws IOException {
        System.out.println("Match-ID erfolgreich gefunden: " + matchid);
        int playerscore = 0 ;
        int computerscore = 0;
        int drawscore = 0;

        int existScore = Score.getInstance().existsPlayerScore(inputPlayerId,connection);
        if (existScore == 0) {
            Score.getInstance().write(inputPlayerId,playerscore,computerscore,drawscore,connection);
        }

        Board board = getBoard(exchange, matchid);
        MoveWriter moveWriter = new MoveWriter();
        Player player = new Player();

        if (player.freeField(board, move)) {
            Position position = new Position(move);
            board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');
            try {
                Connection connection = ConnectionHandler.getConnection();
                moveWriter.newPlayerMove(matchid, move,connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            GamePlayMove winMove = new GamePlayMove(position, '♡');
            if (WinCheck.isWin(board, winMove)) {
                playerscore = 1;
                Score.getInstance().writePlayerscore(inputPlayerId, playerscore,connection);
                try {
                    MatchWrite.getInstance().endMatch(matchid, inputPlayerId, 1,connection);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                int[] score = Score.getInstance().readScore(inputPlayerId,connection);
                RequestUtil.sendResponse(exchange, "Spiel beendet Gewinner bist du Starte ein neues Spiel um weiterzuspielen der Score: "+ Arrays.toString(score), 200);
                return;
            }


            Position computerPosition;
            String computerMove = "";

            do {
                computerPosition = getComputerMove(board, inputPlayerId, matchid);
                if (computerPosition == null) {
                    System.out.println("Computerbewegung nicht gefunden. Versuche es erneut...");
                    continue;
                }

                computerMove = String.valueOf(computerPosition.getRow() + computerPosition.getColumn());
            } while (!player.freeField(board, Integer.parseInt(computerMove)));

            board.getRows().get(computerPosition.getRow()).getFields().get(computerPosition.getColumn()).setGameCharacter('¤');
            try {
                moveWriter.newComputerMove(matchid, Integer.parseInt(computerMove),connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (Computer.winsStrategy(board).isEmpty()) {
                try {
                    MatchWrite.getInstance().endMatch(matchid, inputPlayerId, 3,connection);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                drawscore = 1;
                Score.getInstance().writeDrawscore(inputPlayerId, drawscore,connection);
                int[] score = Score.getInstance().readScore(inputPlayerId,connection);
                RequestUtil.sendResponse(exchange, "Spiel beendet  Starte ein neues Spiel um weiterzuspielen der Score: "+ Arrays.toString(score), 200);
                return;
            }
            if (WinCheck.isWin(board, winMove)) {
                try {
                    MatchWrite.getInstance().endMatch(matchid, inputPlayerId, 6,connection);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                computerscore = 1;
                Score.getInstance().writeComputerscore(inputPlayerId, computerscore,connection);
                int[] score = Score.getInstance().readScore(inputPlayerId,connection);
                RequestUtil.sendResponse(exchange, "Spiel beendet Gewinner ist der Computer Starte ein neues Spiel um weiterzuspielen der Score: "+ Arrays.toString(score), 200);
                return;
            }

            RequestUtil.sendResponse(exchange, " Eingabe akzeptiert: " + move + ". Computer antwortet mit: " + computerMove + ". Gebe eine neue Zahl ein.", 200);
            System.out.println("Match-ID erfolgreich gefunden: " + matchid + "spieler "+move + ". Computer antwortet mit: " + computerMove + ".");
        } else {
            RequestUtil.sendResponse(exchange, " Eingabe nicht akzeptiert " + move + "gebe eine ander Zahl ein", 200);

        }


    }

    private Board getBoard(HttpExchange exchange, int matchid) throws IOException {
        Board board = new Board();
        int[] playerPosition = MoveReader.getInstance().getMoves(matchid, true,connection);
        if (playerPosition != null) {
            for (int x : playerPosition) {
                Position position = new Position(x);
                board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');
            }
        }
        int[] computerPlays = MoveReader.getInstance().getMoves(matchid, false,connection);
        if (computerPlays != null) {
            for (int x : computerPlays) {
                Position position = new Position(x);
                board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('¤');
                //RequestUtil.sendResponse(exchange, "Dies Felder sind vom Computer besetzt " + Arrays.toString(computerPlays) + "und dies von dir " + Arrays.toString(playerPosition) + "setze eine neue zahl", 200);

            }
        }
        return board;
    }

    private Position getComputerMove(Board board, int playerId, int matchId) {

        int matchCounter = MatchReader.getInstance().matchCounter(playerId,connection);
        int moveCounter = 0;
        try {
            moveCounter = MoveReader.getInstance().moveCounter( matchId,connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Computer.getComputerMovement(board, matchCounter, moveCounter);
    }

    public boolean checkInput(HttpExchange exchange, int move) {
        String moveString = String.valueOf(move);
        if (moveString.matches("[1-9]")) {
            return true;
        } else {
            try {
                RequestUtil.sendResponse(exchange, "Ungültige Eingabe: " + move + ". Bitte gib eine Zahl zwischen 1 und 9 ein.");
                return false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }


}


