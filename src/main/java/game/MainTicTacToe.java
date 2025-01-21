package game;

import login.Login;

public class MainTicTacToe {

    static boolean gameLoopStarted;
    public static void main(String[] args) {

        try {
            Login.ask();
            new GameLoop().start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
}
