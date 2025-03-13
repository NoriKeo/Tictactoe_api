package requesthandlers;

import board.Board;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
            exchange.sendResponseHeaders(200, -1);
            return;
        }

        System.out.println("hallo");
        if (!"POST".equals(exchange.getRequestMethod())) {
            System.out.println(exchange.getRequestURI());
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
            int matchid = matchReader.matchStatus(inputPlayerId, 4,ConnectionHandler.getConnection());

            if (matchid == -1) {
                newMatch(exchange, inputPlayerId, move);
            } else {
                handleExistingMatch(exchange, inputPlayerId, matchid, move);
            }


        } catch (JSONException e) {
            RequestUtil.sendInvalidParameterResponse(exchange);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public void newMatch(HttpExchange exchange, int inputPlayerId, int move) throws IOException, SQLException {
        System.out.println("Match-ID nicht gefunden. Neues Spielbrett wird erstellt...");


        Board board = new Board();
        Player player = new Player();

        int matchidnew = 0;
        try {
            matchidnew = MatchWrite.getInstance().createMatch(inputPlayerId,ConnectionHandler.getConnection());
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
            moveWriter.newPlayerMove(matchidnew, move,ConnectionHandler.getConnection());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



        Position computerPosition = getComputerMove(board, inputPlayerId, matchidnew);
        String computerMove = String.valueOf(computerPosition.getRow() + computerPosition.getColumn());
        if (computerPosition != null) {
            board.getRows().get(computerPosition.getRow()).getFields().get(computerPosition.getColumn()).setGameCharacter('¤');
            try {
                moveWriter.newComputerMove(matchidnew, Integer.parseInt(computerMove),ConnectionHandler.getConnection());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Computer movement nicht gefunden");
        }
        //RequestUtil.sendResponse(exchange, "Neue Match-ID erstellt! Eingabe akzeptiert: " + move + ". Computer antwortet mit: " + computerMove + ". Gebe eine neue Zahl ein.", 200);
        ObjectNode responseJson = RequestUtil.objectMapper.createObjectNode();
        responseJson.put("message", "Neue Match-ID erstellt! Eingabe akzeptiert:");
        responseJson.put("matchID", matchidnew);
        responseJson.put("move", move);
        responseJson.put("computerMove", computerMove);
        RequestUtil.sendResponse(exchange, responseJson.toString());


    }

    public void handleExistingMatch(HttpExchange exchange, int inputPlayerId, int matchid, int move) throws IOException, SQLException {
        System.out.println("Match-ID erfolgreich gefunden: " + matchid);
        int playerscore = 0 ;
        int computerscore = 0;
        int drawscore = 0;
        int win = -1;
        int[] score = new int[0];

        int existScore = Score.getInstance().existsPlayerScore(inputPlayerId,ConnectionHandler.getConnection());
        if (existScore == 0) {
            Score.getInstance().write(inputPlayerId,playerscore,computerscore,drawscore,ConnectionHandler.getConnection());
        }

        Board board = getBoard(exchange, matchid);
        MoveWriter moveWriter = new MoveWriter();
        Player player = new Player();

        if (player.freeField(board, move)) {
            Position position = new Position(move);
            board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');
            GamePlayMove winMove = new GamePlayMove(position, '♡');
            if (WinCheck.isWin(board, winMove)) {
                playerscore = 1;
                Score.getInstance().writePlayerscore(inputPlayerId, playerscore,ConnectionHandler.getConnection());
                try {
                    MatchWrite.getInstance().endMatch(matchid, inputPlayerId, 1,ConnectionHandler.getConnection());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                score = Score.getInstance().readScore(inputPlayerId,ConnectionHandler.getConnection());
                //RequestUtil.sendResponse(exchange, "Spiel beendet Gewinner bist du Starte ein neues Spiel um weiterzuspielen der Score: "+ Arrays.toString(score), 200);
                win = 1;
                return;
            }


            Position computerPosition;
            String computerMove = "";


            computerPosition = getComputerMove(board, inputPlayerId, matchid);
            if (computerPosition != null) {
                int moveComputer = computerPosition.getIndex();
                computerMove = String.valueOf(moveComputer);
            } else {
                System.out.println("Computerbewegung nicht gefunden. Versuche es erneut...");
            }




            board.getRows().get(computerPosition.getRow()).getFields().get(computerPosition.getColumn()).setGameCharacter('¤');
            try {
                moveWriter.newComputerMove(matchid, Integer.parseInt(computerMove),ConnectionHandler.getConnection());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (Computer.winsStrategy(board).isEmpty()) {
                try {
                    MatchWrite.getInstance().endMatch(matchid, inputPlayerId, 3,ConnectionHandler.getConnection());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                drawscore = 1;
                Score.getInstance().writeDrawscore(inputPlayerId, drawscore,ConnectionHandler.getConnection());
                score = Score.getInstance().readScore(inputPlayerId,ConnectionHandler.getConnection());
                //RequestUtil.sendResponse(exchange, "Spiel beendet  Starte ein neues Spiel um weiterzuspielen der Score: "+ Arrays.toString(score), 200);
                win = 3;
                return;
            }
            if (WinCheck.isWin(board, winMove)) {
                try {
                    MatchWrite.getInstance().endMatch(matchid, inputPlayerId, 6,ConnectionHandler.getConnection());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                computerscore = 1;
                Score.getInstance().writeComputerscore(inputPlayerId, computerscore,ConnectionHandler.getConnection());
                score = Score.getInstance().readScore(inputPlayerId,ConnectionHandler.getConnection());
                win = 2;
                return;
            }

           // RequestUtil.sendResponse(exchange, " Eingabe akzeptiert: " + move + ". Computer antwortet mit: " + computerMove + ". Gebe eine neue Zahl ein.", 200);
            System.out.println("Match-ID erfolgreich gefunden: " + matchid + "spieler "+move + ". Computer antwortet mit: " + computerMove + ".");
            ObjectNode responseJson = RequestUtil.objectMapper.createObjectNode();
            responseJson.put("message", " Eingabe akzeptiert:");
            responseJson.put("matchID", matchid);
            responseJson.put("move", move);
            responseJson.put("computerMove", computerMove);
            responseJson.put("winner",win);
            responseJson.put("score",Arrays.toString(score) );
            RequestUtil.sendResponse(exchange, responseJson.toString());
        } else {
            System.out.println("feld besetzt");
            ObjectNode responseJson = RequestUtil.objectMapper.createObjectNode();
            responseJson.put("message", " Eingabe nicht akzeptiert:");
            responseJson.put("matchID", matchid);
            responseJson.put("move", -1);
            RequestUtil.sendResponse(exchange, responseJson.toString());


        }


    }

    private Board getBoard(HttpExchange exchange, int matchid) throws IOException, SQLException {
        Board board = new Board();
        int[] playerPosition = MoveReader.getInstance().getMoves(matchid, true,ConnectionHandler.getConnection());
        if (playerPosition != null) {
            for (int x : playerPosition) {
                Position position = new Position(x);
                board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');
            }
        }
        int[] computerPlays = MoveReader.getInstance().getMoves(matchid, false,ConnectionHandler.getConnection());
        if (computerPlays != null) {
            for (int x : computerPlays) {
                Position position = new Position(x);
                board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('¤');

            }
        }
        return board;
    }

    private Position getComputerMove(Board board, int playerId, int matchId) throws SQLException {

        int matchCounter = MatchReader.getInstance().matchCounter(playerId,ConnectionHandler.getConnection());
        int moveCounter = 0;
        try {
            moveCounter = MoveReader.getInstance().moveCounter( matchId,ConnectionHandler.getConnection());
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


