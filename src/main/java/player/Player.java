package player;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import Board.Board;
import Board.Field;
import Board.Position;
import Board.RowFromBoard;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Player {
    private static final Set<String> INPUTS = Set.of("i", "I", "info", "INFO", "Game", "game", "g", "G", "Score", "score", "s", "S");//'♡'

    private static Player instance;
    static String returnInput;
    int input;
    public static Player getInstance() {
        if (instance == null) {
            instance = new Player();
        }
        return instance;
    }

    public static void main(String[] args) throws IOException {
        startHttpServer(8000);
    }

    public static void startHttpServer(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        server.setExecutor(executor);
        server.createContext("/api/input", new InputHandler() );
        server.start();
        System.out.println("Server started at port " + port);
    }



    static class InputHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                returnInput = new String(exchange.getRequestBody().readAllBytes()).trim();
                System.out.println("input bekommen " + returnInput);
                sendResponse(exchange,"input bekommen " + returnInput);
                int input = Integer.parseInt(returnInput);
                Board board = new Board();
                if (!freefield(board, input) && !INPUTS.contains(returnInput)) {
                    sendResponse(exchange,"das feld kann gesetzt werden");
                    System.out.println("das feld kann gesetzt werden");


                }else {
                    sendResponse(exchange,"das feld kann gesetzt werden es ist belget oder keine Zahl");
                    System.out.println("das feld kann gesetzt werden");
                }
            }

        }
    }

   /* public int askInput(Board.Board board) {
        int input2 = 0;
        String input = "";
        System.out.println("♥ Gebe eine Zahl ein oder i für Info ♥");
        System.out.println("♥ Gebe eine Zahl von eins bis neun ein ♥");


        //scScanner.next();




        if (INPUTS.contains(input)) {
            gamesInfo.Infofield.getInstance().info();
        }
        if (!INPUTS.contains(input)) {
            do {
                input2 = Integer.parseInt(input);
            } while (!isvalid(input2));
        }
        if (!freefield(board, input2) && !INPUTS.contains(input)) {
            System.out.println("(¯`·.¸¸.-> °ºDas Feld ist leider schon vergebenº° >-.¸¸.·`¯(");
            //String input = sc.nextLine();
            //input2 = Integer.parseInt(input);
            input2 = askInput(board, scScanner);
        }
        if (INPUTS.contains(input)) {
            input2 = askInput(board, scScanner);
        }

        return input2;
    }
*/

    public boolean isvalid(int input2) {
        return true;
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
}







