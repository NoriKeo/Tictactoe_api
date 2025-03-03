package ControllerandConnection;

import com.sun.net.httpserver.HttpServer;
import login.Playername;
import player.MatchServer;
import player.Player;

import java.io.IOException;
import java.net.InetSocketAddress;
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
        server.createContext("/api/login", new Playername.LoginHandler());
        server.createContext("/api/creataccount", new Playername.CreatAccountHandler());
        server.createContext("/api/newPassword", new Playername.newPasswordHandler());
        server.createContext("/api/input", new Player.InputHandler() );
        server.createContext("/api/matchhistory", new MatchServer.MatchHandler());
        server.createContext("/api/matchHistoryWriter", new MatchServer.Matchwriter());



    }



    public static void main(String[] args) throws IOException {
        ServerController controller = new ServerController();
        controller.endpoints();

    }

}
