package database;

import game.GameTime;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchReaderTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("init.sql")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("testpass");

    MatchReader matchReader;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() throws SQLException {
        ConnectionHandler.jdbcUrl = postgres.getJdbcUrl();
        ConnectionHandler.username = postgres.getUsername();
        ConnectionHandler.password = postgres.getPassword();
        matchReader = MatchReader.getInstance();
        initializeDatabase();
    }

    private void initializeDatabase() throws SQLException {
        try (Connection connection = ConnectionHandler.getConnection()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS match (
                    id SERIAL PRIMARY KEY,
                    player_id INT NOT NULL,
                    verdict_id INT,
                    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    ended TIMESTAMP
                );
            """;
            try (PreparedStatement stmt = connection.prepareStatement(createTableSQL)) {
                stmt.execute();
            }
        }
    }

    private void insertVerdicts() throws SQLException {
        String sql = "INSERT INTO verdict (reason) VALUES (?)";
        String[] reasons = {"win", "lost", "draw", "inGame", "unfinished", "PC Win"};

        try (Connection connection = ConnectionHandler.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (String reason : reasons) {
                pstmt.setString(1, reason);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void insertAccount () throws SQLException {
        String sql = "INSERT INTO accounts (player_name, passwort,security_question) VALUES (?, ?, ?)";
        try (Connection connection = ConnectionHandler.getConnection()){
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, "player");
            pstmt.setString(2, "password");
            pstmt.setString(3, "security_question");
            pstmt.execute();
        }
    }


    private int insertTestMatch(int playerId, Integer verdictId, Timestamp startedAt, Timestamp endedAt) throws SQLException {

        String insertSQL = "INSERT INTO match (player_id, verdict_id, started_at, ended_at) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection connection = ConnectionHandler.getConnection()){
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

        matchReader.timeReader(matchId);

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

        matchReader.matchEndReason(matchId);
        assertEquals(verdictId, MatchReader.endReason, "Endgrund sollte korrekt gesetzt werden.");
    }

    @Test
    void shouldReturnCorrectMatchStatus() throws SQLException {
        int playerId = 1;
        int verdictId = 3;
        insertAccount();
        insertVerdicts();
        int matchId = insertTestMatch(playerId, verdictId, GameTime.start(), null);

        int fetchedMatchId = matchReader.matchStatus(playerId, verdictId);
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

        int matchCount = matchReader.matchCounter(playerId);
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

        int[] matchIds = matchReader.getMates(playerId);

        assertArrayEquals(new int[]{matchId1, matchId2}, matchIds, "Match-IDs sollten korrekt zurückgegeben werden.");
    }
}
