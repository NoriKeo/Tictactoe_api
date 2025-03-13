package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MoveReader {
    private static MoveReader instance;


    public static MoveReader getInstance() {
        if (instance == null) {
            instance = new MoveReader();
        }
        return instance;
    }


    public int findMoveId(int matchid, Connection connection) throws SQLException {
        String selectSQL = "SELECT id FROM move WHERE match_id = ?  ";
        int moveId = -1;
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, matchid);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    moveId = rs.getInt("id");
                }
            }
        }
        return moveId;

    }

    public void newPlayerMove(int matchid, Connection connection) throws SQLException {
        String sql = "SELECT position , created_at , move_nr  FROM move WHERE match_id = ? AND is_player = ?  ";
        try (PreparedStatement insertStmt = connection.prepareStatement(sql)) {
            insertStmt.setInt(1, matchid);
            insertStmt.setBoolean(2, true);

            try (ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    int position = resultSet.getInt("position");
                    Timestamp created = resultSet.getTimestamp("created");
                    int move_nr = resultSet.getInt("move_nr");

                }

            }
        }
    }

    public void newComputerMove(int matchid, Connection connection) throws SQLException {
        String sql = "SELECT position,created_at,move_nr  FROM move WHERE match_id = ? AND is_player = ?  ";
        try (PreparedStatement insertStmt = connection.prepareStatement(sql)) {
            insertStmt.setInt(1, matchid);
            insertStmt.setBoolean(2, false);
            try (ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    int position = resultSet.getInt("position");
                    Timestamp created = resultSet.getTimestamp("created");
                    int move_nr = resultSet.getInt("move_nr");
                }
            }
        }
    }


    public int[] getMoves(int matchid, boolean is_player, Connection connection) {
        String sql = "SELECT position FROM move WHERE match_id = ? AND is_player = ?";
        List<Integer> positions = new ArrayList<>();

        try (PreparedStatement insertStmt = connection.prepareStatement(sql)) {
            insertStmt.setInt(1, matchid);
            insertStmt.setBoolean(2, is_player);
            try (ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    positions.add(resultSet.getInt("position"));
                }
            }
            return positions.stream().mapToInt(i -> i).toArray();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public int moveCounter(int matchid, Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) AS anzahl FROM move WHERE  match_id = ?";
        int counter = 0;
        try (PreparedStatement insertStmt = connection.prepareStatement(sql)) {
            insertStmt.setInt(1, matchid);
            try (ResultSet resultSet = insertStmt.executeQuery()) {
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
}
