package gamesInfo;

import java.util.Scanner;

public class UserInputOutputService {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static UserInputOutputService instance;

    private UserInputOutputService() {
    }

    public static UserInputOutputService getInstance() {
        if (instance == null) {
            instance = new UserInputOutputService();
        }
        return instance;
    }

    public String getInput() {
        return SCANNER.nextLine();
    }

    public void print(String message) {
        System.out.println(message);
    }

    public void printWelcomeMessage() {
        this.print("Wilkommen im infoteil");
        this.print("Wenn vergangene spiele sehen möchtes gebe bitte Game ein");
        this.print("Möchtest du das Scoreboard sehen geben bitte score ein");
        this.print("Möchtest du zum Spiel gebe eine Zahl ein");
    }
}