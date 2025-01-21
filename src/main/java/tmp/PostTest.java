package tmp;

import Board.Board;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class PostTest {

    public static void main(String[] args) throws IOException {
        int serverPort = 8000;

        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);

        server.createContext("/api/hello", new HelloHandler());

        server.setExecutor(Executors.newCachedThreadPool());
        System.out.println("Server läuft " + serverPort);

        server.start();
        Board board = new Board();
        //player.Player.getInstance().askInput(board);
    }

    static class HelloHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response;



            if ("POST".equals(exchange.getRequestMethod())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }

                String input = requestBody.toString().trim();
                System.out.println("Eingabe erhalten: " + input);

                if (input.matches("[1-9]")) {
                    response = "Eingabe akzeptiert: " + input;
                } else {
                    response = "Ungültige Eingabe: " + input;
                }

                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                response = "Nur GET ";
                exchange.sendResponseHeaders(405, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}

