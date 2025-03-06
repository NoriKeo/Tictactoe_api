package database;

import game.GameTime;

import java.sql.*;

public class MatchWrite {

    private static MatchWrite instance;

    public MatchWrite() {
    }

    public static MatchWrite getInstance() {
        if (instance == null) {
            instance = new MatchWrite();
        }
        return instance;
    }


    public int createMatch(int playerId, Connection connection) throws SQLException {
        String sql = "INSERT INTO match (player_id,started_at,verdict_id ) VALUES (?,?,?) ";
        int matchid = 0;
        Timestamp starttime = GameTime.start();
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, playerId);
            ps.setTimestamp(2, starttime);
            ps.setInt(3, 4);
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    matchid = generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return matchid;
    }


    public void endMatch(int matchid, int playerId, int verdict_di, Connection connection) throws SQLException {
        String sql = "UPDATE match SET ended_at = ? , verdict_id = ? WHERE player_id = ? AND id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            Timestamp endtime = GameTime.end();
            ps.setTimestamp(1, endtime);
            ps.setInt(2, verdict_di);
            ps.setInt(3, playerId);
            ps.setInt(4, matchid);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}

