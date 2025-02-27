package database;

import game.GameTime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;

public class MatchReader {


    ArrayList<Integer> playerArray = new ArrayList<>();
    ArrayList<Integer> computerArray = new ArrayList<>();
    private static MatchReader instance;
    ArrayList<String> list;
    ArrayList<String> list2;
    public ArrayList<String> list3;
    public static int computerPlays;
    public static int playerPlays;
    //public static int matchid;
    public static int i = 0;
    public static int endReason;
    File s = new File("test.json");
    int readerjust = 0;

    public MatchReader() {

    }

    public static MatchReader getInstance() {
        if (instance == null) {
            instance = new MatchReader();
        }
        return instance;
    }

    public void matchcounter() throws IOException {


        String content = new String(Files.readAllBytes(Paths.get("test.json"))).trim();
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();


        for (int i = 0; i < 10; i++) {
            if (content.contains("game " + i)) {
                list.add("game " + i);
            }
            if (content.contains("computer " + i)) {
                list2.add("computer " + i);
            }
            if (content.contains("matchhistory " + i)) {
                list3.add("matchhistory " + i);
            }
        }

    }





    public ArrayList getPlayerArray() {
        return playerArray;
    }

    public ArrayList getComputerArray() {
        return computerArray;
    }





    public int matchIDReader(int playerId) throws SQLException {
        String sql = "SELECT id  FROM match WHERE player_id = ? AND started_ad = ? ";
        int matchid = 0;
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
           insertStmt.setInt(1,playerId);
            Timestamp startTime = GameTime.start();
           insertStmt.setTimestamp(2,startTime);

           try(ResultSet resultSet = insertStmt.executeQuery()) {
               while (resultSet.next()) {
                    matchid = resultSet.getInt("id");
                    return matchid;
               }

           }
        }
        return matchid;
    }
    public void timeReader(int machid) throws SQLException {
        String sql = "SELECT started_at,ended  FROM match WHERE id = ? ";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1,machid);
            try(ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    GameTime.getStart = resultSet.getTimestamp("started_at");
                    GameTime.getEnd = resultSet.getTimestamp("ended");
                }
            }
        }

    }
    public void matchEndReason (int matchid) throws SQLException {
        String sql = "SELECT verdict_id FROM match WHERE id = ? ";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1,matchid);
            try(ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    endReason = resultSet.getInt("verdict_id");
                }
            }
        }
    }

    public int matchStatus(int playerId, int verdict_id)  {
        String sql = "SELECT id  FROM match WHERE player_id = ? AND verdict_id = ? ";
        int matchid ;
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1,playerId);
            insertStmt.setInt(2,verdict_id);
            try(ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                     matchid = resultSet.getInt("id");
                    return matchid;
                }

            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int matchCounter(int playerid){
        String sql = "SELECT COUNT(*) AS anzahl FROM match WHERE playerid = ?";
        int counter = 0;
        try(Connection connection = ConnectionHandler.getConnection()){
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1,playerid);
            try(ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    counter = resultSet.getInt("anzahl");
                    return counter;
                }
            }

        } catch (SQLException e) {
            return counter;

        }

        return counter;
    }


  /*  public static void read(int playerId) throws SQLException {
        String querySQL = "SELECT computer_plays, player_plays, match_id FROM match_history WHERE player_id = ?";

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(querySQL);
            pstmt.setInt(1,playerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    //matchid = rs.getInt("match_id");
                    computerPlays = rs.getInt("computer_plays");
                    playerPlays = rs.getInt("player_plays");
                }
            }
        }
    }*/

}






