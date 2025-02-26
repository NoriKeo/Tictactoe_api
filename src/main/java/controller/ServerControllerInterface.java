package controller;

import java.io.IOException;

public interface ServerControllerInterface {
    void serverStart(int port) throws IOException;
    void endpoints();
}
