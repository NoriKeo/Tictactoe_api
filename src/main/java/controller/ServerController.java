package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import database.InitializeDatabase;
import database.LiquibaseMigrationService;
import requesthandlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController implements ServerControllerInterface{
        HttpServer server;
        ExecutorService executor;

        @Override
    public void serverStart(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
         executor = Executors.newFixedThreadPool(5);
        server.setExecutor(executor);
        server.start();
    }

    @Override
    public void endpoints(){
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/creataccount", new CreatAccountHandler());
        server.createContext("/api/newPassword", new NewPasswordHandler());
        server.createContext("/api/matchHandler" ,new MatchHandler());
        server.createContext("/api/score", new ScoreHandler());


        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                // Setze die CORS-Header für jede Anfrage
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");

                if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(200, -1); // Antwort auf OPTIONS-Anfrage
                    return;
                }

                // Weiter zur nächsten Anfragebehandlung
                exchange.sendResponseHeaders(404, -1); // Default: 404, falls keine passende Route
            }
        });
    }



    public static void main(String[] args) throws IOException, SQLException {
        ServerController controller = new ServerController();
        controller.serverStart(8000);
        System.out.println("Server started");
        LiquibaseMigrationService migrationService = new LiquibaseMigrationService();
        migrationService.runMigration();
        //InitializeDatabase.initializeTables();
        controller.endpoints();


        ///write.endMatch();
    }

}
