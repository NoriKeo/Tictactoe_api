package database;

import game.GameTime;

import java.io.File;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatchReader {


    private static MatchReader instance;
    ;

    public static int i = 0;
    public static int endReason;

    public MatchReader() {

    }

    public static MatchReader getInstance() {
        if (instance == null) {
            instance = new MatchReader();
        }
        return instance;
    }


    public void timeReader(int machid, Connection connection) throws SQLException {
        String sql = "SELECT started_at,ended_at  FROM match WHERE id = ? ";
        try (PreparedStatement insertStmt = connection.prepareStatement(sql);
        ) {
            insertStmt.setInt(1, machid);
            try (ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    GameTime.getStart = resultSet.getTimestamp("started_at");
                    GameTime.getEnd = resultSet.getTimestamp("ended_at");
                }
            }
        }

    }

    public void matchEndReason(int matchid, Connection connection) throws SQLException {
        String sql = "SELECT verdict_id FROM match WHERE id = ? ";
        try (PreparedStatement insertStmt = connection.prepareStatement(sql);
        ) {
            insertStmt.setInt(1, matchid);
            try (ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    endReason = resultSet.getInt("verdict_id");
                }
            }
        }
    }

    public int matchStatus(int playerId, int verdict_id, Connection connection) {
        String sql = "SELECT id  FROM match WHERE player_id = ? AND verdict_id = ? ";
        int matchid;
        try (PreparedStatement insertStmt = connection.prepareStatement(sql)) {
            insertStmt.setInt(1, playerId);
            insertStmt.setInt(2, verdict_id);
            try (ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    matchid = resultSet.getInt("id");
                    return matchid;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int matchCounter(int playerid, Connection connection) {
        String sql = "SELECT COUNT(*) AS anzahl FROM match WHERE player_id = ?";
        int counter = 0;
        try (PreparedStatement insertStmt = connection.prepareStatement(sql)) {
            insertStmt.setInt(1, playerid);
            try (ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    counter = resultSet.getInt("anzahl");
                    return counter;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return counter;

        }

        return counter;
    }

    public int[] getMates(int player_id, Connection connection) {
        String sql = "SELECT id FROM match WHERE player_id = ? ";
        List<Integer> matches = new ArrayList<>();

        try (PreparedStatement insertStmt = connection.prepareStatement(sql)) {
            insertStmt.setInt(1, player_id);
            try (ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    matches.add(resultSet.getInt("id"));
                }
            }
            return matches.stream().mapToInt(i -> i).toArray();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}






