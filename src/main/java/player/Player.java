package player;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Board.Board;
import Board.Field;
import Board.RowFromBoard;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import gamesInfo.Position;

public class Player {
    private static final Set<String> INPUTS =
        Set.of("i", "I", "info", "INFO", "Game", "game", "g", "G", "Score", "score", "s", "S");//'♡'
    static String returnInput;

    public static void main(String[] args) throws IOException {
        startHttpServer(8000);
    }

    public static void startHttpServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        server.setExecutor(executor);
        server.createContext("/api/input", new InputHandler());
        server.start();
        System.out.println("Server started at port " + port);
    }

    public static boolean freefield(Board board, int input2) {
        List<Position> freeFields = new ArrayList<>();
        for (RowFromBoard row : board.getRows()) {
            for (Field field : row.getFields()) {
                if (field.isEmpty()) {
                    freeFields.add(field.getPosition());
                }
            }
        }
        for (Position field : freeFields) {
            if (field.getIndex() == input2) {
                return true;
            }
        }
        return false;
    }

    public static void sendResponse(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, response, 200);
    }

    public static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static class InputHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                returnInput = new String(exchange.getRequestBody().readAllBytes()).trim();
                System.out.println("input bekommen " + returnInput);
                sendResponse(exchange, "input bekommen " + returnInput);
                int input = Integer.parseInt(returnInput);
                Board board = new Board();
                if (!freefield(board, input) && !INPUTS.contains(returnInput)) {
                    sendResponse(exchange, "das feld kann gesetzt werden");
                    System.out.println("das feld kann gesetzt werden");

                } else {
                    sendResponse(exchange, "das feld kann gesetzt werden es ist belget oder keine Zahl");
                    System.out.println("das feld kann gesetzt werden");
                }
            }

        }
    }
}







