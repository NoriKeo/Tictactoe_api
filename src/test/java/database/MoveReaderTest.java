package database;

import game.GameTime;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MoveReaderTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("init.sql")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("testpass");

    private MoveReader moveReader;

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
        moveReader = MoveReader.getInstance();
        LiquibaseMigrationServiceTests liquibaseMigrationService = new LiquibaseMigrationServiceTests();
        liquibaseMigrationService.runTestMigration(postgres);
        insertAccount();
        insertVerdicts();
        initializeMatch();
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


    private void initializeMatch() throws SQLException {
   String sql  ="INSERT INTO match (player_id,started_at,ended_at,verdict_id) VALUES (?, ?,?,?)";
        Timestamp startTime = GameTime.start();
        Timestamp endTime = new Timestamp(System.currentTimeMillis());
   try (Connection connection = ConnectionHandler.getConnection()){
       PreparedStatement pstmt = connection.prepareStatement(sql);
       pstmt.setInt(1, 1);
       pstmt.setTimestamp(2, startTime);
       pstmt.setTimestamp(3, endTime);
       pstmt.setInt(4, 1);
       pstmt.executeUpdate();
   }
    }

    private int insertTestMove(int matchId, int position, int moveNr, boolean isPlayer) throws SQLException {
        String sql = "INSERT INTO move (match_id, position, move_nr, is_player) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, matchId);
            pstmt.setInt(2, position);
            pstmt.setInt(3, moveNr);
            pstmt.setBoolean(4, isPlayer);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("Fehler beim Einfügen des Zuges.");
    }

    @Test
    void testFindMoveId() throws SQLException {
        int matchId = 1;
        int moveId = insertTestMove(matchId, 5, 1, true);

        moveReader.findMoveId(matchId);

        assertNotEquals(0, moveId, "Move ID sollte existieren.");
    }

    @Test
    void testGetMoves() throws SQLException {
        initializeMatch();
        int matchId = 2;
        insertTestMove(matchId, 3, 1, true);
        insertTestMove(matchId, 7, 2, true);

        int[] moves = moveReader.getMoves(matchId, true);

        assertArrayEquals(new int[]{3, 7}, moves, "Spielerzüge sollten korrekt zurückgegeben werden.");
    }

    @Test
    void testMoveCounter() throws SQLException {
        for (int i = 0 ; i < 5 ; i++) {
            initializeMatch();
        }
        int matchId = 3;
        insertTestMove(matchId, 2, 1, false);
        insertTestMove(matchId, 4, 2, false);

        int count = moveReader.moveCounter(matchId);

        assertEquals(2, count, "Anzahl der Züge sollte 2 sein.");
    }
}
