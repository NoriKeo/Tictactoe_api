package database;

import game.GameTime;

import java.sql.*;

public class MoveWriter {






    public void newPlayerMove(int matchid,int position) {
        String insertOrUpdateSQL = "INSERT INTO move (match_id, is_player,position,created_at) VALUES (?,?,?,?)";
        Timestamp starttime = GameTime.start();
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
        Timestamp starttime = GameTime.start();
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
