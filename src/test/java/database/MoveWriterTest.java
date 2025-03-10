package database;

import game.GameTime;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MoveWriterTest {



    private MoveWriter moveWriter;


    @BeforeEach
    void setUp() throws SQLException, InterruptedException {

        moveWriter = new MoveWriter();
        Thread.sleep(10000);

        LiquibaseMigrationServiceTests liquibaseMigrationService = new LiquibaseMigrationServiceTests();
        liquibaseMigrationService.runTestMigration(LiquibaseMigrationServiceTests.postgres);
        insertAccount();
        insertVerdicts();
        initializeMatch();
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

    private void insertVerdicts() throws SQLException {
        String sql = "INSERT INTO verdict (reason) VALUES (?)";
        String[] reasons = {"win", "lost", "draw", "inGame", "unfinished", "PC Win"};

        try (Connection connection = LiquibaseMigrationServiceTests.getConnection();
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
        try (Connection connection = LiquibaseMigrationServiceTests.getConnection()){
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
        try (Connection connection = LiquibaseMigrationServiceTests.getConnection()) {
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
        try (Connection connection = LiquibaseMigrationServiceTests.getConnection()) {
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
        moveWriter.newPlayerMove(matchId, 5,LiquibaseMigrationServiceTests.getConnection());

        int moveCount = countMoves(matchId, true);
        assertEquals(1, moveCount, "Es sollte genau ein Spielerzug gespeichert sein.");
    }

    @Test
    void testNewComputerMove() throws SQLException {
        int matchId = 1;

       // insertTestMove(matchId,1,false);
        moveWriter.newComputerMove(matchId, 7,LiquibaseMigrationServiceTests.getConnection());

        int moveCount = countMoves(matchId, false);
        assertEquals(1, moveCount, "Es sollte genau ein Computerzug gespeichert sein.");
    }
}
