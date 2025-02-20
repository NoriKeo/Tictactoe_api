package readAndWrite;

import ControllerandConnection.ConnectionHandler;

import java.sql.*;

public class MoveReader {

    static int move_id;


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

    public void findMoveId() throws SQLException {
        String selectSQL = "SELECT id FROM match WHERE match_id = ? , move_nr = ? ";
        try(Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement(selectSQL);
            stmt.setInt(1, MatchHistoryReader.matchid);
            stmt.setInt(1,/*move_nr*/);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                   move_id = rs.getInt("id");
                }
            }
        }

    }

    public void newPlayerMove() throws SQLException {
        String sql = "SELECT is_player,position,created  FROM move WHERE move_nr = ?, match_id = ?  ";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1,move_id);
            insertStmt.setInt(2, MatchHistoryReader.matchid);

            try(ResultSet resultSet = insertStmt.executeQuery()) {
                while (resultSet.next()) {
                    /*matchid = resultSet.getInt("id");*/
                }

            }
        }
    }
}
