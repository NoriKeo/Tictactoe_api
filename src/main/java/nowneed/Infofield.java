/*
package nowneed;

import Board.Board;
import readAndWrite.MatchReader;
//import readAndWrite.ScoreBoardPrinter;
import Board.Print;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Set;

public class Infofield {
    private static final Set<String> INFO = Set.of("i", "I", "info", "INFO");
    private static final Set<String> GAME = Set.of("Game", "game", "g", "G");
    private static final Set<String> SCORE = Set.of("Score", "score", "s", "S");
    private final UserInputOutputService userIOService;
    private final Print printi;
    public static boolean scoreprint = false;
    private static Infofield INSTANCE;
    Scanner scanner;

    private Infofield(UserInputOutputService userInputOutputService, Print print) {
        this.userIOService = userInputOutputService;
        this.printi = print;
    }

    public static Infofield getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Infofield(UserInputOutputService.getInstance(), Print.getInstancePrint());
        }
        return INSTANCE;
    }

    public void info() {
        Board board = new Board();

        userIOService.printWelcomeMessage();
        String input = userIOService.getInput();

        if (GAME.contains(input)) {
            try {
                MatchReader.getInstance().matchcounter();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (!MatchReader.getInstance().list3.isEmpty()) {
                Print.getInstancePrint().matchHistory();
            }
            printi.matchHistory();
            return;
        }

        if (SCORE.contains(input)) {
            scoreprint = true;
            try {
                ScoreBoardPrinter.getInstance().getPrintetScore(board);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        if (INFO.contains(input)) {
            userIOService.printWelcomeMessage();
            return;
        }

        //player.Player.getInstance().askInput(board);
    }
}
*/
