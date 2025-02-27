package database;

//import nowneed.Match;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class ScoreBoardWriter {
    File s = new File("Score.txt");
    static int scorex;
    static int scorey;
    PrintWriter pWriter;
    static int draw;
    BufferedWriter writer;
    static int computer_score;
    static int player_score;
    private static ScoreBoardWriter instance;
    static int draw_score;

    public ScoreBoardWriter() {
    }


    public static ScoreBoardWriter getInstance() {
        if (instance == null) {
            instance = new ScoreBoardWriter();
        }
        return instance;
    }


    public void scoreCounter() {
       /* if (Match.playerWin) {
            scorex++;
        }
        if (Match.computerWin) {
            scorey++;
        }
        if (Computer.draw) {
            draw++;
        }
        computer_score = scorey;
        player_score = scorex;
        draw_score = draw;*/
    }









    public static void writer() throws SQLException {
        String insertOrUpdateSQL;
       /* if (Match.rounds == 0) {
            insertOrUpdateSQL = "INSERT INTO score (player_id, computer_score, player_score, draw_score) " +
                    "VALUES (?, ?, ?,?) ";

            try (Connection connection = ConnectionHandler.getConnection()) {
                PreparedStatement pstmt = connection.prepareStatement(insertOrUpdateSQL);
                pstmt.setInt(1, Playername.playerId);
                pstmt.setInt(2, computer_score);
                pstmt.setInt(3, player_score);
                pstmt.setInt(4, draw_score);
                pstmt.executeUpdate();
            }
        } else {

            insertOrUpdateSQL = "UPDATE score SET computer_score = ?, player_score = ?,draw_score = ? WHERE player_id = ?";

            try (Connection connection = ConnectionHandler.getConnection()) {
                PreparedStatement pstmt = connection.prepareStatement(insertOrUpdateSQL);
                pstmt.setInt(1, computer_score);
                pstmt.setInt(2, player_score);
                pstmt.setInt(3, draw_score);
                pstmt.setInt(4, Playername.playerId);

                pstmt.executeUpdate();
            }

        }*/
    }







}



