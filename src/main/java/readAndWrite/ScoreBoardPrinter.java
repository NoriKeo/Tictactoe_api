package readAndWrite;

import ControllerandConnection.ConnectionHandler;
import login.Playername;
import player.Computer;
import Board.Board;
import nowneed.Infofield;
import nowneed.Match;

import java.io.BufferedReader;
import java.io.File;
import java.sql.*;


public class ScoreBoardPrinter {

    String playerScore;
    String computerScore;
    String drawScore;
    BufferedReader br;
    File s = new File("Score.txt");
    private final ScoreBoardWriter boardWriter;

    private static ScoreBoardPrinter instance;

    public ScoreBoardPrinter(ScoreBoardWriter scoreBoardWriter) {
        this.boardWriter = scoreBoardWriter;
    }

    public static ScoreBoardPrinter getInstance() {
        if (instance == null) {
            instance = new ScoreBoardPrinter(ScoreBoardWriter.getInstance());
        }
        return instance;
    }

    public static void initializeDatabase() throws SQLException {
        try (Connection connection = ConnectionHandler.getConnection()) {
            Statement stmt = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS score (" +
                    "score_id SERIAL PRIMARY KEY not null, " +
                    "player_id int NOT NULL  REFERENCES accounts(player_id), " +
                    "computer_score int, " +
                    "player_score int," +
                    " draw_score int) ";
            stmt.execute(createTableSQL);
        }
    }

    public void read() throws SQLException {
        String querySQL = "SELECT computer_score, player_score , draw_score FROM score WHERE player_id = ?";

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(querySQL);
            pstmt.setInt(1, Playername.playerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    computerScore = String.valueOf(rs.getInt("computer_score"));
                    playerScore = String.valueOf(rs.getInt("player_score"));
                    drawScore = String.valueOf(rs.getInt("draw_score"));
                }
            }
        }
    }


    public void winInfoPrint(Board board) throws SQLException {
        initializeDatabase();
        read();

        if (Match.playerWin && !Computer.draw) {
            System.out.println("Der Gewinner ist ♡ mit einem score von " + playerScore + " ( •̀ᄇ• ́)ﻭ✧ ");
            System.out.println("Der score von ¤ ist " + computerScore);
        }
        if (Match.computerWin && !Computer.draw) {
            System.out.println("Der Gewinner ist ¤ mit einem score von " + computerScore + "╭( ･ㅂ･)و ̑̑ ＂");
            System.out.println("Der scorer von ♡ ist " + playerScore);
        }
        if (Computer.winsStrategy(board).isEmpty() && !Match.playerWin && !Match.computerWin || Computer.draw) {
            System.out.println("★·.·´¯`·.·★unentschieden★·.·`¯´·.·★");
            System.out.println("es steht zum " + drawScore + " mal unentscheiden (❁ᴗ͈ ˬ ᴗ͈)ᶻᶻᶻ✧");
            System.out.println("Der scorer von ♡ ist " + playerScore);
            System.out.println("Der score von ¤ ist " + computerScore);
        }
        if (Infofield.scoreprint && !Match.playerWin && !Match.computerWin && !Computer.winsStrategy(board).isEmpty()) {
            System.out.println("(¯´•._.•Score•._.•´¯(");
            System.out.println("Der scorer von ♡ ist " + playerScore);
            System.out.println("Der score von ¤ ist " + computerScore);
            System.out.println("es gab " + drawScore + " ein unentscheiden");
            System.out.println("♥ ----------------------------------- ♥");
        }
       /* nowneed.Match.computerWin = false;
        nowneed.Match.playerWin = false;*/

    }

    public void getPrintetScore(Board board) throws SQLException {
        //boardWriter.scoreWrite();
        ScoreBoardWriter.initializeDatabase();
        boardWriter.scoreCounter();
        ScoreBoardWriter.writer();
        winInfoPrint(board);
    }


}
