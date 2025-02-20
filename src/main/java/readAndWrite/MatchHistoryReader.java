package readAndWrite;

import login.Playername;
import ControllerandConnection.ConnectionHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;

public class MatchHistoryReader {


    ArrayList<Integer> playerArray = new ArrayList<>();
    ArrayList<Integer> computerArray = new ArrayList<>();
    private static MatchHistoryReader instance;
    ArrayList<String> list;
    ArrayList<String> list2;
    public ArrayList<String> list3;
    public static int computerPlays;
    public static int playerPlays;
    public static int matchid;
    public static int i = 0;
    public static int endReason;
    File s = new File("test.json");
    int readerjust = 0;

    public MatchHistoryReader() {

    }

    public static MatchHistoryReader getInstance() {
        if (instance == null) {
            instance = new MatchHistoryReader();
        }
        return instance;
    }

    public void matchcounter() throws IOException {


        String content = new String(Files.readAllBytes(Paths.get("test.json"))).trim();
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        list3 = new ArrayList<>();


        for (int i = 0; i < 10; i++) {
            if (content.contains("player " + i)) {
                list.add("player " + i);
            }
            if (content.contains("computer " + i)) {
                list2.add("computer " + i);
            }
            if (content.contains("matchhistory " + i)) {
                list3.add("matchhistory " + i);
            }
        }

    }




   /* public void breck() throws IOException {
        if (s.exists() && s.length() > 0) {
            String content = new String(Files.readAllBytes(Paths.get("test.json"))).trim();
            InputStream is = new FileInputStream("test.json");
            jsonReader = Json.createReader(is);
            objectreader = jsonReader.readObject();
            jsonReader.close();
            is.close();
            if (content.contains("playerFieldsbreck" + " Name " + login.Playername.name) && content.contains("computerFieldsbreck" + " Name " + login.Playername.name)) {
                playerbreck = objectreader.getJsonArray("playerFieldsbreck" + " Name " + login.Playername.name);
                computerbreck = objectreader.getJsonArray("computerFieldsbreck" + " Name " + login.Playername.name);
                if (playerbreck == null && computerbreck == null) {

                }
            }

        }

    }*/


    public ArrayList getPlayerArray() {
        return playerArray;
    }

    public ArrayList getComputerArray() {
        return computerArray;
    }




    public static void initializeDatabase() throws SQLException {
        try (Connection connection = ConnectionHandler.getConnection()) {
            Statement stmt = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS match (" +
                    "id SERIAL PRIMARY KEY, " +
                    "player_id INT NOT NULL, " +
                    "started_at timestamp , " +
                    "ended timestamp, " +
                    "verdict_id int ," +
                    "FOREIGN KEY (player_id) REFERENCES accounts(player_id)" +
                    "FOREIGN KEY (verdict_id) REFERENCES verdict(id)" +
                    ");";
            stmt.execute(createTableSQL);

        }
    }
    public void matchIDReader () throws SQLException {
        String sql = "SELECT id  FROM match WHERE player_id = ? , started_ad = ? ";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
           insertStmt.setInt(1,Playername.playerId);
           insertStmt.setTimestamp(2,MatchTime.start);

           try(ResultSet resultSet = insertStmt.executeQuery()) {
               while (resultSet.next()) {
                   matchid = resultSet.getInt("id");
               }

           }
        }
    }
    public void timeReader () throws SQLException {
        String sql = "SELECT started_at,ended  FROM match WHERE id = ? ";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1,matchid);
            try(ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    MatchTime.getStart = resultSet.getTimestamp("started_at");
                    MatchTime.getEnd = resultSet.getTimestamp("ended");
                }
            }
        }

    }
    public void matchEndReason () throws SQLException {
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


    public static void read() throws SQLException {
        String querySQL = "SELECT computer_plays, player_plays, match_id FROM match_history WHERE player_id = ?";

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(querySQL);
            pstmt.setInt(1, Playername.playerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    matchid = rs.getInt("match_id");
                    computerPlays = rs.getInt("computer_plays");
                    playerPlays = rs.getInt("player_plays");
                }
            }
        }
    }

}






