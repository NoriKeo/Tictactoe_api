package readAndWrite;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ControllerandConnection.ConnectionHandler;
import login.Playername;

public class MatchHistoryRead {

    public static int computerPlays;
    public static int playerPlays;
    public static int matchid;
    public static int i = 0;
    private static MatchHistoryRead instance;
    public ArrayList<String> list3;
    ArrayList<Integer> playerArray = new ArrayList<>();
    ArrayList<Integer> computerArray = new ArrayList<>();
    ArrayList<String> list;
    ArrayList<String> list2;
    File s = new File("test.json");
    int readerjust = 0;

    public MatchHistoryRead() {

    }

    public static MatchHistoryRead getInstance() {
        if (instance == null) {
            instance = new MatchHistoryRead();
        }
        return instance;
    }

    public static void read() throws SQLException {
        String querySQL = "SELECT computer_plays, player_plays, match_id FROM match_history WHERE player_id = ?";

        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(querySQL);
            pstmt.setInt(1, Playername.playerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    matchid = rs.getInt("match_id");
                    computerPlays = rs.getInt("computer_plays");
                    playerPlays = rs.getInt("player_plays");
                }
            }
        }
    }

}






