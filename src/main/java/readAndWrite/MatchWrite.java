package readAndWrite;

import requesthandlers.RequestUtil;
//import gamesInfo.BoardhistoryArray;
import ControllerandConnection.ConnectionHandler;

import java.io.File;
import java.sql.*;
import java.util.concurrent.locks.ReentrantLock;

public class MatchWrite {
    static File s = new File("test.json");
    private static final ReentrantLock lock = new ReentrantLock();
    //static JSONObject object;
    static String name = RequestUtil.name;

    private static MatchWrite instance;

    public MatchWrite() {
    }

    public static MatchWrite getInstance() {
        if (instance == null) {
            instance = new MatchWrite();
        }
        return instance;
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
    public int createMatch(int playerId) throws SQLException {
        String sql = "INSERT INTO match (started_at, player_id,verdict_id ) VALUES (?,?,?) ";
        String sqlMatchId = "SELECT id FROM match WHERE started_at = ? AND player_id = ? ";
        int matchid = 0;
        Timestamp starttime = MatchTime.start();
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setTimestamp(1,starttime);
            ps.setInt(2, playerId);
            ps.setInt(3,4 );
            ps.executeUpdate();

        }
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sqlMatchId);
            ps.setTimestamp(1, starttime);
            ps.setInt(2, playerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                     matchid = rs.getInt("id");
                }
            }
        }

        return matchid;
    }


    public void endMatch (int matchid, int playerId, int verdict_di) throws SQLException {
        String sql = "UPDATE match SET ended_at = ?, verdict_id = ? WHERE player_id = ?, id = ?";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            Timestamp endtime = MatchTime.end();
            ps.setTimestamp(1,endtime);
            ps.setInt(2, verdict_di);
            ps.setInt(3, playerId);
            ps.setInt(4,matchid);
            ps.executeUpdate();

        }
    }


    /*public static void writer() throws SQLException {
        int playerplay = Integer.parseInt(BoardhistoryArray.playerplay);
        int computerPlays = Integer.parseInt(BoardhistoryArray.computer_play);
        String insertOrUpdateSQL = "INSERT INTO match_history (player_id, computer_plays, player_plays, win, winPlayer,winComputer,) VALUES (?, ?, ?,?,?,?) ";
        boolean win = false;
        System.out.println(playerplay + "testiiiii 2");
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(insertOrUpdateSQL);
            pstmt.setInt(1, Playername.playerId);
            pstmt.setInt(2, computerPlays);
            pstmt.setInt(3, playerplay);
            pstmt.setBoolean(4, win);
            pstmt.executeUpdate();
        }
    }

    public static void updater() {
        int playerPlayUpdatet = Integer.parseInt(BoardhistoryArray.playerplay);
        int computerPlayUpdatet = Integer.parseInt(BoardhistoryArray.computer_play);
        try {
            MatchHistoryReader.initializeDatabase();
            MatchHistoryReader.read();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String updateSQL = "UPDATE match_history SET player_plays = ?, computer_plays = ?, win = ? WHERE  player_id = ? AND match_id = ? ";
        boolean win = false;

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(updateSQL);
            pstmt.setInt(1, playerPlayUpdatet);
            pstmt.setInt(2, computerPlayUpdatet);
            pstmt.setBoolean(3, win);
            pstmt.setInt(4, Playername.playerId);
            pstmt.setInt(5, MatchHistoryReader.matchid);
            pstmt.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
*/


  /*  public static boolean checkUnresolvedWins() throws SQLException {
        String checkSQL = "SELECT * FROM match_history WHERE player_id = ? AND win = false;";

        try (Connection connection = ControllerandConnection.ConnectionHandler.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(checkSQL);
            pstmt.setInt(1, login.Playername.playerId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }*/


}

