package ControllerandConnection;

import com.sun.net.httpserver.HttpServer;
import requesthandlers.CreatAccountHandler;
import requesthandlers.LoginHandler;
import requesthandlers.NewPasswordHandler;
import player.MatchServer;
import player.Player;
import readAndWrite.MatchHistoryReader;
import readAndWrite.MatchHistoryWrite;

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
        server.createContext("/api/input", new Player.InputHandler() );
        server.createContext("/api/matchhistory", new MatchServer.MatchHandler());
        server.createContext("/api/matchHistoryWriter", new MatchServer.Matchwriter());



    }



    public static void main(String[] args) throws IOException, SQLException {
        ServerController controller = new ServerController();
        controller.endpoints();
        MatchHistoryWrite write = new MatchHistoryWrite();
        MatchHistoryReader reader = new MatchHistoryReader();
        MatchHistoryWrite.createMatch();
        MatchHistoryWrite.initializeDatabase();

        ///write.endMatch();
        write.createMatch();
    }

}
