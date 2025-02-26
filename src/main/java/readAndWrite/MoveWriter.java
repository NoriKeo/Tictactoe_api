package readAndWrite;

import ControllerandConnection.ConnectionHandler;

import java.sql.*;

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


    public void newPlayerMove(int matchid,int position) {
        String insertOrUpdateSQL = "INSERT INTO move (match_id, is_player,position,created_at) VALUES (?,?,?,?)";
        Timestamp starttime = MatchTime.start();
        try(Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(insertOrUpdateSQL);
            ps.setInt(1, matchid);
            ps.setBoolean(2, true);
            ps.setInt(3, position);
            ps.setTimestamp(4,starttime);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void newComputerMove(int matchid,int position) {
        String insertOrUpdateSQL = "INSERT INTO move (match_id, is_player,position,created_at) VALUES (?,?,?,?)";
        Timestamp starttime = MatchTime.start();
        try(Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(insertOrUpdateSQL);
            ps.setInt(1, matchid);
            ps.setBoolean(2, false);
            ps.setInt(3, position);
            ps.setTimestamp(4,starttime);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
