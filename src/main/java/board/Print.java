package board;

import database.ConnectionHandler;

import java.sql.*;
import java.util.ArrayList;


public class Print {
    static int nummber;
    private static Print instance;
    public static Board boardbreck;
    public static Board boardBreck;
    static ArrayList<Integer> numbersplayer = new ArrayList();
    static int[] playerNumbers = new int[30];
    static int[] computerNumbers = new int[30];
    static int computerPlays;
    static int playerPlays;
    public Print() {

    }

    public static Print getInstancePrint() {
        if (instance == null) {
            instance = new Print();
        }
        return instance;
    }
    /*String testi2 = String.valueOf(testi);
        System.out.println(testi2.length() + " länge");


        for (char c : testi2.toCharArray()) {
        System.out.println(c + ".......");
    }*/

    /*public void matchHistory() {

        nummber++;

        Board board = new Board();
        try {
            MatchReader.initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            MatchReader.getInstance().read();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (int k = 0; k < 9; k++) {
            String playerPlays = String.valueOf(MatchReader.getInstance().playerPlays);

            int[] plays = new int[playerPlays.length()];

            for (int i = 0; i < playerPlays.length(); i++) {
                plays[i] = Character.getNumericValue(playerPlays.charAt(i));
        }

            for (int playerplay : plays) {
                board.getField(new Position(playerplay)).setGameCharacter('♡');

            }

            String computerPlays = String.valueOf(MatchReader.getInstance().computerPlays);

            int[] plays2 = new int[computerPlays.length()];

            for (int i = 0; i < computerPlays.length(); i++) {
                plays2[i] = Character.getNumericValue(computerPlays.charAt(i));
            }

            for (int playComputer : plays2) {
                board.getField(new Position(playComputer)).setGameCharacter('¤');
        }


            System.out.println("Board.Board " + MatchReader.getInstance().matchid);
        board.print();
            try {
                MatchReader.read();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            //numberUsed();


            MatchReader.getInstance().list3.remove("matchhistory " + MatchReader.getInstance().i);
            MatchReader.getInstance().i++;
        try {
            //JsonFileRead.getInstance().jsonRead();
            MatchReader.read();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        }


    }*/

    public static void initializeDatabase() throws SQLException {
        try (Connection connection = ConnectionHandler.getConnection()) {
            Statement stmt = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS match_history (" +
                    "match_id SERIAL PRIMARY KEY, " +
                    "player_id INT NOT NULL, " +
                    "computer_plays INT, " +
                    "player_plays INT, " +
                    "win boolean," +
                    "FOREIGN KEY (player_id) REFERENCES accounts(player_id)" +
                    ");";
            stmt.execute(createTableSQL);
        }
    }

    /*public static Board breckBoard() {

        String querySQL = "SELECT computer_plays, player_plays FROM match_history WHERE player_id = ? AND win = false";
        int computerPlays = -1;
        int playerPlays = -1;

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(querySQL);
            pstmt.setInt(1, RequestUtil.playerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    computerPlays = rs.getInt("computer_plays");
                    playerPlays = rs.getInt("player_plays");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (computerPlays == -1 || playerPlays == -1) {
            return null;
        }

        String playerBreck = String.valueOf(playerPlays);
        String computerBreck = String.valueOf(computerPlays);

        boardBreck = new Board();

        int[] plays = new int[playerBreck.length()];

        for (int i = 0; i < playerBreck.length(); i++) {
            plays[i] = Character.getNumericValue(playerBreck.charAt(i));
        }

        for (int playPlayer : plays) {
            boardBreck.getField(new Position(playPlayer)).setGameCharacter('♡');
        }

        int[] plays2 = new int[computerBreck.length()];

        for (int i = 0; i < computerBreck.length(); i++) {
            plays2[i] = Character.getNumericValue(computerBreck.charAt(i));
        }

        for (int playComputer : plays2) {
            boardBreck.getField(new Position(playComputer)).setGameCharacter('¤');
        }


      *//*  for (char playerField : playerBreck.toCharArray()) {
            int inputPlayer = Character.getNumericValue(playerField); // Convert char to int
            boardBreck.getField(new Board.Position(inputPlayer)).setGameCharacter('♡');
        }
        for (char computerField : computerBreck.toCharArray()) {
            int inputComputer = Character.getNumericValue(computerField); // Convert char to int
            boardBreck.getField(new Board.Position(inputComputer)).setGameCharacter('¤');
        }*//*

        boardBreck.print();
        return boardBreck;
    }
*/

    /*public static void numberUsed() {
        int number = Integer.parseInt(readAndWrite.ScoreBoardPrinter.getInstance().playerScore);
        int number1 = Integer.parseInt(readAndWrite.ScoreBoardPrinter.getInstance().computerScore);
        if (number > number1) {
            for (int i = 1; i <= 9; i++) {
                int traget = i;
                long conut = Arrays.stream(playerNumbers).filter(num -> num == traget).count();
                if (conut > nowneed.Match.match) {
                    System.out.println("der Spieler hat am meisten gewonnen un da bei nutzete er " + traget + " genau " + conut + " mal");

                }
            }
        }
        if (number < number1) {
            for (int i = 1; i <= 9; i++) {
                int traget = i;
                long conut = Arrays.stream(computerNumbers).filter(num -> num == traget).count();
                if (conut > nowneed.Match.match && traget != 5) {
                    System.out.println("der player.Computer hat am meisten gewonnen un da bei nutzete er " + traget + " genau " + conut + " mal");
                }
            }
        }
    }*/


   /* public static void main(String[] args) {
        getInstancePrint().matchHistory();
        //numberUsed();

    }*/
}
