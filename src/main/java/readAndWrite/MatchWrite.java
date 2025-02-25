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
    public int createMatch(int playerId)  {
        String sql = "INSERT INTO match (player_id,started_at,verdict_id ) VALUES (?,?,?) ";
        int matchid = 0;
        Timestamp starttime = MatchTime.start();
        try(Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, playerId);
            ps.setTimestamp(2, starttime);
            ps.setInt(3, 4);
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    matchid = generatedKeys.getInt(1);
                }
            }

        }catch (SQLException e) {
            e.printStackTrace();
        }

        return matchid;
    }


    public void endMatch (int matchid, int playerId, int verdict_di) {
        String sql = "UPDATE match SET ended_at = ? AND verdict_id = ? WHERE player_id = ? AND id = ?";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            Timestamp endtime = MatchTime.end();
            ps.setTimestamp(1,endtime);
            ps.setInt(2, verdict_di);
            ps.setInt(3, playerId);
            ps.setInt(4,matchid);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}

