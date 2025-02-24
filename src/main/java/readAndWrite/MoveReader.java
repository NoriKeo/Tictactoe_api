package readAndWrite;

import ControllerandConnection.ConnectionHandler;

import java.lang.foreign.StructLayout;
import java.sql.*;

public class MoveReader {



    public static void initializeDatabase() throws SQLException {
        try (Connection connection = ConnectionHandler.getConnection()) {
            Statement stmt = connection.createStatement();
            String createTableSQL = "CREATE TABLE IF NOT EXISTS move (" +
                    "id SERIAL PRIMARY KEY, " +
                    "match_id INT NOT NULL, " +
                    "is_player boolean NOT NULL, " +
                    "position int NOT NULL, " +
                    "created_at int NOT NULL," +
                    "move_nr SERIAL "+
                    "FOREIGN KEY (match_id) REFERENCES match(id)" +
                    ");";
            stmt.execute(createTableSQL);
        }
    }

    public void findMoveId( int matchid) throws SQLException {
        String selectSQL = "SELECT id FROM match WHERE match_id = ? , move_nr = ? ";
        try(Connection connection = ConnectionHandler.getConnection()) {
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
        String sql = "SELECT position,created,move_nr  FROM move WHERE match_id = ?, is_player = ?  ";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1,matchid);
            insertStmt.setBoolean(2, true);

            try(ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    int position = resultSet.getInt("position");
                    Timestamp created = resultSet.getTimestamp("created");
                    int move_nr = resultSet.getInt("move_nr");

                }

            }
        }
    }

    public void newComputerMove(int matchid) throws SQLException {
        String sql = "SELECT position,created,move_nr  FROM move WHERE match_id = ?, is_player = ?  ";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1,matchid);
            insertStmt.setBoolean(2, false);
            try(ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    int position = resultSet.getInt("position");
                    Timestamp created = resultSet.getTimestamp("created");
                    int move_nr = resultSet.getInt("move_nr");
                }
            }
        }
    }
}
