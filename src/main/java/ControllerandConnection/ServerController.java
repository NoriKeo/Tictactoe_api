package ControllerandConnection;

import com.sun.net.httpserver.HttpServer;
import login.Playername;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerController {
        HttpServer server;
        ExecutorService executor;

    public void serverStart(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
         executor = Executors.newFixedThreadPool(5);
        server.setExecutor(executor);
        server.start();
    }

    public void endpoints(){
        server.createContext("/api/login", new Playername.LoginHandler());
        server.createContext("/api/creataccount", new Playername.CreatAccountHandler());
        server.createContext("/api/newPassword");

    }

    public static void main(String[] args) throws IOException {
        ServerController controller = new ServerController();
        controller.endpoints();
    }

}
