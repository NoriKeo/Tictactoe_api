package readAndWrite;

import ControllerandConnection.ConnectionHandler;

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

    public static void initializeDatabase() throws SQLException {
        try (Connection connection = ConnectionHandler.getConnection()) {
            Statement stmt = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS move (" +
                    "id SERIAL PRIMARY KEY, " +
                    "match_id INT NOT NULL, " +
                    "is_player boolean NOT NULL, " +
                    "position int NOT NULL, " +
                    "created_at int NOT NULL," +
                    "move_nr SERIAL " +
                    "FOREIGN KEY (match_id) REFERENCES match(id)" +
                    ");";
            stmt.execute(createTableSQL);
        }
    }

    public void findMoveId(int matchid) throws SQLException {
        String selectSQL = "SELECT id FROM match WHERE match_id = ? AND move_nr = ? ";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(selectSQL);
            stmt.setInt(1, matchid);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int move_id = rs.getInt("id");
                }
            }
        }

    }

    public void newPlayerMove(int matchid) throws SQLException {
        String sql = "SELECT position , created , move_nr  FROM move WHERE match_id = ? AND is_player = ?  ";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
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

    public void newComputerMove(int matchid) throws SQLException {
        String sql = "SELECT position,created,move_nr  FROM move WHERE match_id = ? AND is_player = ?  ";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
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



    public int[] getMoves(int matchid, boolean is_player)  {
        String sql = "SELECT position FROM move WHERE match_id = ? AND is_player = ?";
        List<Integer> positions = new ArrayList<>();

        try(Connection connection = ConnectionHandler.getConnection()){
            PreparedStatement insertStmt = connection.prepareStatement(sql);
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


    public int moveCounter(int playerid, int matchid) {
        String sql = "SELECT COUNT(*) AS anzahl FROM match WHERE playerid = ? and match_id = ?";
        int counter = 0;
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1, playerid);
            insertStmt.setInt(2, matchid);
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
