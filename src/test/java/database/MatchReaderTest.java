package database;

import game.GameTime;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchReaderTest {



    MatchReader matchReader;

    Connection connection;

    {
        try {
            connection = LiquibaseMigrationServiceTests.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @BeforeEach
    void setUp() throws SQLException, InterruptedException {
        matchReader = MatchReader.getInstance();
        Thread.sleep(10000);
        LiquibaseMigrationServiceTests liquibaseMigrationService = new LiquibaseMigrationServiceTests();
        liquibaseMigrationService.runTestMigration(LiquibaseMigrationServiceTests.postgres);
        //initializeDatabase();
    }



    private void insertVerdicts() throws SQLException {
        String sql = "INSERT INTO verdict (reason) VALUES (?)";
        String[] reasons = {"win", "lost", "draw", "inGame", "unfinished", "PC Win"};

        try (Connection connection = LiquibaseMigrationServiceTests.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
                for (String reason : reasons) {
                    pstmt.setString(1, reason);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
        }



    }

    private void insertAccount () throws SQLException {
        String sql = "INSERT INTO accounts (player_name, passwort,security_question) VALUES (?, ?, ?)";
        try (Connection connection = LiquibaseMigrationServiceTests.getConnection()){
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, "player");
            pstmt.setString(2, "password");
            pstmt.setString(3, "security_question");
            pstmt.execute();
        }
    }


    private int insertTestMatch(int playerId, Integer verdictId, Timestamp startedAt, Timestamp endedAt) throws SQLException {

        String insertSQL = "INSERT INTO match (player_id, verdict_id, started_at, ended_at) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection connection = LiquibaseMigrationServiceTests.getConnection()){
             PreparedStatement pstmt = connection.prepareStatement(insertSQL);
            pstmt.setInt(1, playerId);
            pstmt.setInt(2, verdictId);
            pstmt.setTimestamp(3, startedAt);
            pstmt.setTimestamp(4, endedAt);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }

        throw new SQLException("Fehler beim Einfügen des Match-Datensatzes.");
    }



    @Test
    void shouldReadMatchTimeCorrectly() throws SQLException {
        int playerId = 1;
        Timestamp startTime = GameTime.start();
        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        insertAccount();
        insertVerdicts();
        int matchId = insertTestMatch(playerId, 1, startTime, endTime);

        matchReader.timeReader(matchId,connection);

        assertEquals(startTime, GameTime.getStart, "Startzeit sollte korrekt gesetzt werden.");
        assertEquals(endTime, GameTime.getEnd, "Endzeit sollte korrekt gesetzt werden.");
    }

    @Test
    void shouldReturnCorrectMatchEndReason() throws SQLException {
        int playerId = 5;
        int verdictId = 2;
        for (int i = 0; i < 5; i++) {
            insertAccount();
        }
        insertVerdicts();
        int matchId = insertTestMatch(playerId, verdictId, GameTime.start(), null);

        matchReader.matchEndReason(matchId,connection);
        assertEquals(verdictId, MatchReader.endReason, "Endgrund sollte korrekt gesetzt werden.");
    }

    @Test
    void shouldReturnCorrectMatchStatus() throws SQLException {
        int playerId = 1;
        int verdictId = 3;
        insertAccount();
        insertVerdicts();
        int matchId = insertTestMatch(playerId, verdictId, GameTime.start(), null);

        int fetchedMatchId = matchReader.matchStatus(playerId, verdictId,connection);
        assertEquals(matchId, fetchedMatchId, "Match-ID sollte für den gegebenen Status korrekt sein.");
    }

    @Test
    void shouldReturnMatchCountForPlayer() throws SQLException {
        int playerId = 5;
        for (int i = 0; i < 5; i++) {
            insertAccount();
        }
        insertVerdicts();
        insertTestMatch(playerId, 1, GameTime.start(), null);
        insertTestMatch(playerId, 1, GameTime.start(), null);

        int matchCount = matchReader.matchCounter(playerId,connection);
        assertEquals(2, matchCount, "Der Spieler sollte zwei Matches haben.");
    }

    @Test
    void shouldReturnCorrectMatchIdsForPlayer() throws SQLException {
        int playerId = 6;
        for (int i = 0; i < 6; i++) {
            insertAccount();
        }
        insertVerdicts();
        int matchId1 = insertTestMatch(playerId, 5, GameTime.start(), null);
        int matchId2 = insertTestMatch(playerId, 1, GameTime.start(), null);

        int[] matchIds = matchReader.getMates(playerId,connection);

        assertArrayEquals(new int[]{matchId1, matchId2}, matchIds, "Match-IDs sollten korrekt zurückgegeben werden.");
    }
}
