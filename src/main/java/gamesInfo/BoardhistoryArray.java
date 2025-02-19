package gamesInfo;

import Board.Board;
import nowneed.Match;
import player.Computer;
import player.MatchServer;
import readAndWrite.MatchHistoryWrite;

import java.io.IOException;
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
    static Position computerPosition;
    public


    public static void safeGamePlayPlayer() {
       /* //player_play = "" + nowneed.Match.input;
        //playerFields.add(nowneed.Match.input);
        if (Match.rounds == 0) {
            playerplay = String.valueOf(Match.input);


        } else {
            playerplay = playerplay + Match.input;

        }
        System.out.println(playerplay + "testttti");*/
        if (Match.rounds == 0) {
            for (int i : MatchServer.playerPlaysList) {
                playerplay = String.valueOf(i);
                MatchServer.playerPlaysList.remove(Integer.valueOf(i));
                if (MatchServer.playerPlaysList.size() == 0) {
                    break;
                }

            }
        }else {
            for (int i : MatchServer.playerPlaysList) {
                playerplay = playerplay + i;
                if (MatchServer.playerPlaysList.size() == 0) {
                    break;
                }
            }
            MatchServer.playerPlaysList.removeAll(MatchServer.playerPlaysList);
        }
        System.out.println(playerplay + "testttti");


    }




    public static void test() {
        String matchtest = "";
        List<Integer> testList = new ArrayList<>();
        testList.add(1);
        for (int i = 0; i <= testList.size(); i++) {
        if (Match.rounds == 0) {
            for (int x : testList) {
                matchtest = String.valueOf(x);
                testList.remove(Integer.valueOf(x));
                if (testList.isEmpty()) {
                    break;
                }
            }
        }else {
            for (int x : testList) {
                matchtest = matchtest + x;
                if (testList.size() == 0) {
                    break;
                }
            }
        }
        System.out.println(matchtest + " test ausgabe " + Match.rounds);
            testList.removeAll(testList);
            if (Match.rounds == 0) {
            testList.add(2);
        }
        if (Match.rounds == 1) {
            testList.add(3);
            testList.add(7);
            testList.add(9);
        }
        Match.rounds++;
        }
        System.out.println(testList + " test " + Match.rounds);

    }
    public static void main(String[] args) throws IOException {
        test();
    }

    public static void safeGamePlayComputer(Board board) {
        computerPosition = Computer.getComputerMovement(board);
        int i = computerPosition.getIndex();
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
            nowneed.Match.roundprintsafe++;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    public static void fieldbrecks() {
        playerFieldsbreck.add(Match.input);
        int i = computerPosition.getIndex();
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