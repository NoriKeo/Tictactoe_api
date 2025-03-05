package database;

import game.GameTime;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MoveWriterTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("init.sql")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("testpass");

    private MoveWriter moveWriter;

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
        moveWriter = new MoveWriter();
        initializeDatabase();
        insertAccount();
        insertVerdicts();
        initializeMatch();
    }

    private void initializeDatabase() throws SQLException {
        try (Connection connection = ConnectionHandler.getConnection()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS move (
                    id SERIAL PRIMARY KEY,
                    match_id INT NOT NULL,
                    position INT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    is_player BOOLEAN NOT NULL
                );
            """;
            try (PreparedStatement stmt = connection.prepareStatement(createTableSQL)) {
                stmt.execute();
            }
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

    private int insertTestMove(int matchId, int position, boolean isPlayer) throws SQLException {
        String sql = "INSERT INTO move (match_id, position, is_player) VALUES (?, ?, ?) RETURNING id";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, matchId);
            pstmt.setInt(2, position);
            pstmt.setBoolean(3, isPlayer);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("Fehler beim Einfügen des Zuges.");
    }


    private int countMoves(int matchId, boolean isPlayer) throws SQLException {
        String sql = "SELECT COUNT(*) AS anzahl FROM move WHERE match_id = ? AND is_player = ?";
        try (Connection connection = ConnectionHandler.getConnection()) {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, matchId);
            pstmt.setBoolean(2, isPlayer);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("anzahl");
                }
            }
        }
        return 0;
    }

    @Test
    void testNewPlayerMove() throws SQLException {
        int matchId = 1;
        moveWriter.newPlayerMove(matchId, 5);

        int moveCount = countMoves(matchId, true);
        assertEquals(1, moveCount, "Es sollte genau ein Spielerzug gespeichert sein.");
    }

    @Test
    void testNewComputerMove() throws SQLException {
        int matchId = 1;

        insertTestMove(matchId,1,false);
        moveWriter.newComputerMove(matchId, 7);

        int moveCount = countMoves(matchId, false);
        assertEquals(1, moveCount, "Es sollte genau ein Computerzug gespeichert sein.");
    }
}
