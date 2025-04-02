package requesthandlers;

import board.Board;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.sql.SQLException;


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

        Position playerPositionnow;

        MoveWriter moveWriter = new MoveWriter();
        Position position = new Position(move);
        playerPositionnow = position;
        board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');
        try {
            moveWriter.newPlayerMove(matchidnew, move,ConnectionHandler.getConnection());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



        Position computerPosition;
        String computerMove = "";
        int moveComputer = 0;



        System.out.println("Comppppputer test new match " + matchidnew);
        do {
            System.out.println("Compppputer ----------testnew match " + matchidnew + " " + move);
            computerPosition = getComputerMove(board, inputPlayerId, matchidnew);
            moveComputer = computerPosition.getIndex();
            System.out.println("ComputerPositionnew match: " + moveComputer);
        } while (computerPosition == null || computerPosition.equals(playerPositionnow));
        moveComputer = computerPosition.getIndex();
        computerMove = String.valueOf(moveComputer);
        System.out.println("computer testnew match " + computerMove);
        try {
            moveWriter.newComputerMove(matchidnew, Integer.parseInt(computerMove),ConnectionHandler.getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        //RequestUtil.sendResponse(exchange, "Neue Match-ID erstellt! Eingabe akzeptiert: " + move + ". Computer antwortet mit: " + computerMove + ". Gebe eine neue Zahl ein.", 200);
        String response = "Neue Match-ID erstellt! Eingabe akzeptiert:";
        int[] playerPosition = getplayerPosition(matchidnew);

        int[] computerPlays = getcomputerPosition(matchidnew);

        sendResponse(exchange,response,matchidnew,move,moveComputer,-1,null,playerPosition,computerPlays);



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
        Position playerPositionnow;

        if (player.freeField(board, move)) {
            Position position = new Position(move);
            playerPositionnow = position;
            board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');
            GamePlayMove winMove = new GamePlayMove(position, '♡');
            try {
                moveWriter.newPlayerMove(matchid, move,ConnectionHandler.getConnection());

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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
                String response = "player gewinnt";
                int[] playerPosition = getplayerPosition(matchid);
                int[] computerPlays = getcomputerPosition(matchid);
                sendResponse(exchange,response,matchid,move,-1,win,score,playerPosition,computerPlays);
                return;
            }


            Position computerPosition;
            String computerMove = "";
            int moveComputer = 0;


            System.out.println("Comppppputer test " + matchid);
            do {
                System.out.println("Compppputer ----------test " + matchid + " " + move);
                computerPosition = getComputerMove(board, inputPlayerId, matchid);
                moveComputer = computerPosition.getIndex();
                System.out.println("ComputerPosition: " + moveComputer);
            } while (computerPosition == null || computerPosition.equals(playerPositionnow));
            moveComputer = computerPosition.getIndex();
            computerMove = String.valueOf(moveComputer);
            System.out.println("computer test " + computerMove);





            board.getRows().get(computerPosition.getRow()).getFields().get(computerPosition.getColumn()).setGameCharacter('¤');
            try {
                moveWriter.newComputerMove(matchid, Integer.parseInt(computerMove),ConnectionHandler.getConnection());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            GamePlayMove computerwinmove = new GamePlayMove(computerPosition, '¤');



            if (WinCheck.isWin(board, computerwinmove)) {
                try {
                    MatchWrite.getInstance().endMatch(matchid, inputPlayerId, 2,ConnectionHandler.getConnection());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                computerscore = 1;
                Score.getInstance().writeComputerscore(inputPlayerId, computerscore,ConnectionHandler.getConnection());
                score = Score.getInstance().readScore(inputPlayerId,ConnectionHandler.getConnection());
                win = 2;
                String response = "computer gewinnt";
                int[] playerPosition = getplayerPosition(matchid);
                int[] computerPlays = getcomputerPosition(matchid);
                sendResponse(exchange,response,matchid,move,moveComputer,win,score,playerPosition,computerPlays);
                return;
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
                String response = "Unentschieden";
                int[] playerPosition = getplayerPosition(matchid);
                int[] computerPlays = getcomputerPosition(matchid);

                sendResponse(exchange,response,matchid,move,moveComputer,win,score,playerPosition,computerPlays);
                return;
            }

            // RequestUtil.sendResponse(exchange, " Eingabe akzeptiert: " + move + ". Computer antwortet mit: " + computerMove + ". Gebe eine neue Zahl ein.", 200);
            System.out.println("Board test:" + board);
            System.out.println("Match-ID erfolgreich gefunden: " + matchid + "spieler "+move + ". Computer antwortet mit: " + computerMove + ".");
            String response = "Eingabe akzeptiert:";
            int[] playerPosition = getplayerPosition(matchid);
            int[] computerPlays = getcomputerPosition(matchid);
            sendResponse(exchange,response,matchid,move,moveComputer,win,score,playerPosition,computerPlays);
        } else {
            System.out.println("feld besetzt");
            String response = "feld besetzt";
            int[] playerPosition = getplayerPosition(matchid);
            int[] computerPlays = getcomputerPosition(matchid);
            sendResponse(exchange,response,matchid,-1,-1,win,score,playerPosition,computerPlays);



        }


    }

    private Board getBoard(HttpExchange exchange, int matchid) throws IOException, SQLException {
        Board board = new Board();
        int[] playerPosition = getplayerPosition(matchid);
        if (playerPosition != null) {
            for (int x : playerPosition) {
                Position position = new Position(x);
                board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('♡');
            }
        }
        int[] computerPlays = getcomputerPosition(matchid);
        if (computerPlays != null) {
            for (int x : computerPlays) {
                Position position = new Position(x);
                board.getRows().get(position.getRow()).getFields().get(position.getColumn()).setGameCharacter('¤');

            }
        }
        return board;
    }

    private  int[] getplayerPosition(int matchid) throws SQLException {
        int[] playerPosition = MoveReader.getInstance().getMoves(matchid, true,ConnectionHandler.getConnection());

        return playerPosition;

    }

    private int[] getcomputerPosition(int matchid) throws SQLException {
        int[] computerPlays = MoveReader.getInstance().getMoves(matchid, false,ConnectionHandler.getConnection());
        return computerPlays;
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

    public void sendResponse(HttpExchange exchange, String response, int matchid, int move, int computerMove, int win , int[] score, int[] playerPosition,int[] computerPlays ) throws IOException {
        ObjectMapper objectMapper = RequestUtil.objectMapper;
        ObjectNode responseJson = objectMapper.createObjectNode();
        responseJson.put("message", response);
        responseJson.put("matchID", matchid);
        responseJson.put("move", move);
        responseJson.put("computerMove", computerMove);
        responseJson.put("winner",win);
        responseJson.set("score", objectMapper.valueToTree(score));
        responseJson.set("playerPosition", objectMapper.valueToTree(playerPosition));
        responseJson.set("computerPlays", objectMapper.valueToTree(computerPlays));
        System.out.println("tessssti" + responseJson);
        RequestUtil.sendResponse(exchange, responseJson.toString());

    }


}

