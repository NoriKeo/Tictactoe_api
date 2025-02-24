package readAndWrite;

import ControllerandConnection.ConnectionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class MoveWriter {



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


    public void newPlayerMove(int matchid) throws SQLException {
        String insertOrUpdateSQL = "INSERT INTO macth (match_id, is_player, creatred_at) VALUES (?,?,?)";
        try(Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(insertOrUpdateSQL);
            ps.setInt(1, matchid);
            ps.setBoolean(2, true);
            ps.setTimestamp(3,MatchTime.start);

        }
    }

    public void newComputerMove(int matchid) throws SQLException {
        String insertOrUpdateSQL = "INSERT INTRO match(match_id, is_player, creatred_at) VALUES (?,?,?)";
        try(Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(insertOrUpdateSQL);
            ps.setInt(1, matchid);
            ps.setBoolean(2, false);
            ps.setTimestamp(3,MatchTime.start);
        }
    }
}
