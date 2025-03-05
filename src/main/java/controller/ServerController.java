package controller;

import com.sun.net.httpserver.HttpServer;
import database.InitializeDatabase;
import database.LiquibaseMigrationService;
import requesthandlers.CreatAccountHandler;
import requesthandlers.LoginHandler;
import requesthandlers.MatchHandler;
import requesthandlers.NewPasswordHandler;

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



    }



    public static void main(String[] args) throws IOException, SQLException {
        ServerController controller = new ServerController();
        controller.serverStart(8000);
        System.out.println("Server started");
        LiquibaseMigrationService migrationService = new LiquibaseMigrationService();
        migrationService.runMigration();
        InitializeDatabase.initializeTables();
        controller.endpoints();


        ///write.endMatch();
    }

}
