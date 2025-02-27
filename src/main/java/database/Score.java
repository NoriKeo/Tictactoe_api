package database;

import java.sql.*;

public class Score {


    private static Score instance;

    public static Score getInstance() {
        if (instance == null) {
            instance = new Score();
        }
        return instance;
    }

    public int existsPlayerScore( int playerid) {
        String sql = "SELECT COUNT(*) AS anzahl FROM score WHERE player_id = ?";
        int counter = 0;
        try(Connection connection = ConnectionHandler.getConnection()){
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1,playerid);
            try(ResultSet resultSet = insertStmt.executeQuery()) {
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

    public void write( int playerId, int playerscore,int computerscore,int drawscore ) {
        String sql = "INSERT INTO score (player_id,computer_score,player_score,draw_score ) VALUES (?,?,?,?) ";
        try(Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1,playerId);
            insertStmt.setInt(2,computerscore);
            insertStmt.setInt(3,playerscore);
            insertStmt.setInt(4,drawscore);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



    public void writePlayerscore(int playerid,int playerscore) {
        String sql = "UPDATE score SET player_score = player_score + ? WHERE player_id = ?";
        try(Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1,playerscore);
            insertStmt.setInt(2,playerid);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void writeComputerscore(int playerid,int computerscore) {
        String sql = "UPDATE score SET computer_score = computer_score + ? WHERE player_id = ?";
        try(Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1,computerscore);
            insertStmt.setInt(2,playerid);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void writeDrawscore(int playerid,int drawscore) {
        String sql = "UPDATE score SET draw_score = draw_score + ? WHERE player_id = ?";
        try(Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement insertStmt = connection.prepareStatement(sql);
            insertStmt.setInt(1,drawscore);
            insertStmt.setInt(2,playerid);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public int[] readScore(int playerid) {
        String sql = "SELECT player_score, computer_score, draw_score FROM score WHERE player_id = ?";
        int[] scores = {0, 0, 0};

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement selectStmt = connection.prepareStatement(sql);
            selectStmt.setInt(1, playerid);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                scores[0] = resultSet.getInt("player_score");
                scores[1] = resultSet.getInt("computer_score");
                scores[2] = resultSet.getInt("draw_score");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return scores;
    }

}
