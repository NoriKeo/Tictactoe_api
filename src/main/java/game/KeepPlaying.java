package game;

import Board.Board;
import ControllerandConnection.ConnectionHandler;
import gamesInfo.BoardhistoryArray;
import nowneed.GameLoop;
import readAndWrite.MatchHistoryRead;
import readAndWrite.MatchHistoryWrite;
import readAndWrite.ScoreBoardPrinter;
import nowneed.Match;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class KeepPlaying {

    private static final Set<String> INPUTS = Set.of("Ja", "ja", "Yes", "j", "jaa", " ");
    static long time;
    static long seconds;
    static DateTimeFormatter df;


    public static boolean keepPlaying(Board board) {

        try {
            ScoreBoardPrinter.getInstance().getPrintetScore(board);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //timeStemp();
        try {
            MatchHistoryWrite.initializeDatabase();
            MatchHistoryWrite.updater();
            //JsonWrite.jsonWriter();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            MatchHistoryRead.initializeDatabase();
            MatchHistoryRead.read();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement winUpate = connection.prepareStatement(
                    "UPDATE match_history SET win = true WHERE match_id = ?  ");
            winUpate.setInt(1, MatchHistoryRead.matchid);
            winUpate.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        seconds = TimeUnit.MILLISECONDS.toMinutes(time);
        Match.match++;
        System.out.println("(っ◔◡◔)っ ♥ Möchtest du weiter spielen ♥");
        //to do
        //String input = player.Player.scScanner.nextLine();
        String input = "nein";


        if (INPUTS.contains(input)) {
            GameLoop gameLoop = new GameLoop();
            System.out.println("˜”*°• Viel Spaß •°*”˜");
            Match.rounds = 0;
            BoardhistoryArray.playerFields = new ArrayList<>();

            BoardhistoryArray.computerFields = new ArrayList<>();

            try {
                gameLoop.start();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (KeepPlaying.keepPlaying(Match.getBoard())) {
                //readAndWrite.MatchHistoryWrite.object.remove("PID");
            }
            return true;
        }

        System.out.println("╰☆☆Vielen Dank fürs Spielen☆☆╮");
       /* try {
            Match_History_Write.initializeDatabase();
            Match_History_Write.writer();
            //JsonWrite.jsonWriter();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/
        System.exit(0);
        //return false;
        return false;
    }

    public static void timeStemp() {
        time = Match.t2 - Match.t1;
        //timeStemp();
        LocalDateTime now = LocalDateTime.now();
        df = DateTimeFormatter.ofPattern("dd.MM.yyyy kk:mm");
        int seconds7 = Math.toIntExact((time / 1000) % 60);
        System.out.println(now.format(df));
        System.out.println("Dieses nowneed.Match wurde in einer zeit von " + seconds7 + " sekunden bestritten └│∵│┐┌│∵│┘");
       /* try {
            TimeSafe.writer();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/


    }

    public static void main(String[] args) {
        timeStemp();
    }


}
