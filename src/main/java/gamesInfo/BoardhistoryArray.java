package gamesInfo;

import readAndWrite.MatchHistoryWrite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BoardhistoryArray {


    public static List<Integer> playerFields = new ArrayList<>();
    public static List<Integer> computerFields = new ArrayList<>();
    static List<Integer> playerFieldsbreck = new ArrayList<>();
    static List<Integer> computerFieldsbreck = new ArrayList<>();
    public static String playerplay;
    public static String computer_play;
    public static void safeGamePlayPlayer() {
        //player_play = "" + gamesInfo.Match.input;
        //playerFields.add(gamesInfo.Match.input);
        if (Match.rounds == 0) {
            playerplay = String.valueOf(Match.input);


        } else {
            playerplay = playerplay + Match.input;

        }
        System.out.println(playerplay + "testttti");


    }

    public static void safeGamePlayComputer() {
        int i = Match.computerPosition.getIndex();
        //computerFields.add(i);
        if (Match.rounds == 0) {
            computer_play = String.valueOf(i);
        } else {
            computer_play = computer_play + i;
        }


    }

    public static void safer() {
        /*try {
            JsonWrite.jsonWriter();
            gamesInfo.Match.roundprintsafe++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    public static void fieldbrecks() {
        playerFieldsbreck.add(Match.input);
        int i = Match.computerPosition.getIndex();
        computerFieldsbreck.add(i);
        try {
            MatchHistoryWrite.initializeDatabase();
            MatchHistoryWrite.writer();
            //JsonWrite.jsonWriter();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static int test = 1;
    static int testi;

    public static void test1() {
        for (int i = 0; i < 10; i++) {
            test = i;
            if (i == 0) {
                playerplay = "0";

            } else {
                playerplay = playerplay + test;

            }
            testi = Integer.parseInt(playerplay);
            System.out.println(testi);

        }
    }







}