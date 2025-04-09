package tmp;

import board.Board;
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
            if (!"POST".equals(exchange.getRequestMethod())) {
                String response = "Nur POST-Anfragen sind erlaubt!";
                exchange.sendResponseHeaders(405, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }

            // Lese die Eingabe aus dem RequestBody
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
            String input = requestBody.toString().trim();

            System.out.println("Eingabe erhalten: " + input);

            // Überprüfe, ob die Eingabe eine gültige Zahl zwischen 1 und 9 ist
            String response;
            if (input.matches("[1-9]")) {
                response = "Eingabe akzeptiert: " + input + ". Gebe eine neue Zahl ein.";
            } else {
                response = "Ungültige Eingabe: " + input + ". Bitte gib eine Zahl zwischen 1 und 9 ein.";
            }

            // Sende die Antwort
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }


    public void handle(HttpExchange exchange) throws IOException {

        String response;

    }
}

