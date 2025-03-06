package database;

import org.junit.jupiter.api.*;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchWriteTest {


    MatchWrite matchWrite;
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

        matchWrite = MatchWrite.getInstance();
        //initializeDatabase();
        Thread.sleep(10000);

        LiquibaseMigrationServiceTests liquibaseMigrationService = new LiquibaseMigrationServiceTests();
        liquibaseMigrationService.runTestMigration(LiquibaseMigrationServiceTests.postgres);
    }


    private void insertVerdicts() throws SQLException {
        String sql = "INSERT INTO verdict (reason) VALUES (?)";
        String[] reasons = {"win", "lost", "draw", "inGame", "unfinished", "PC Win"};

        try (Connection connection = LiquibaseMigrationServiceTests.getConnection()){
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

    @Test
    void testCreateMatch() throws SQLException {
        int playerId = 1;
        insertAccount();
        insertVerdicts();
        int matchId = matchWrite.createMatch(playerId,connection);
        assertTrue(matchId > 0, "Match-ID sollte größer als 0 sein");

        try (Connection connection = LiquibaseMigrationServiceTests.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM match WHERE id = ?")) {
            pstmt.setInt(1, matchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "Match sollte in der Datenbank existieren");
                assertEquals(playerId, rs.getInt("player_id"), "Spieler-ID sollte übereinstimmen");
                assertNotNull(rs.getTimestamp("started_at"), "Startzeit sollte nicht null sein");
            }
        }
    }

    @Test
    void testEndMatch() throws SQLException {
        int playerId = 1;
        insertVerdicts();
        insertAccount();
        int matchId = matchWrite.createMatch(playerId,connection);
        int verdictId = 2;


        matchWrite.endMatch(matchId, playerId, verdictId, connection);

        try (Connection connection = LiquibaseMigrationServiceTests.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM match WHERE id = ?")) {
            pstmt.setInt(1, matchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                assertTrue(rs.next(), "Match sollte in der Datenbank existieren");
                assertNotNull(rs.getTimestamp("ended_at"), "Endzeit sollte gesetzt sein");
                assertEquals(verdictId, rs.getInt("verdict_id"), "Verdict-ID sollte aktualisiert sein");
            }
        }
    }

}
