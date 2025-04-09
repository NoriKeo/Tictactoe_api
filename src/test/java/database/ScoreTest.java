package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

class ScoreTest {

    private Score score;

    @BeforeEach
    void setUp() throws SQLException, InterruptedException {
        score = Score.getInstance();
        Thread.sleep(1000);
        LiquibaseMigrationServiceTests liquibaseMigrationService = new LiquibaseMigrationServiceTests();
        liquibaseMigrationService.runTestMigration(LiquibaseMigrationServiceTests.postgres);
        for (int i = 0; i < 6; i++) {
            insertAccount();
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
    void shouldInsertNewScore() throws SQLException {
        int playerId = 1;
        score.write(playerId, 10, 5, 3, LiquibaseMigrationServiceTests.getConnection());

        int[] scores = score.readScore(playerId, LiquibaseMigrationServiceTests.getConnection());
        assertArrayEquals(new int[]{10, 5, 3}, scores, "Scores sollten korrekt in die DB eingefügt werden.");
    }

    @Test
    void shouldUpdatePlayerScore() throws SQLException {
        int playerId = 2;
        score.write(playerId, 10, 5, 3, LiquibaseMigrationServiceTests.getConnection());
        score.writePlayerscore(playerId, 5, LiquibaseMigrationServiceTests.getConnection());

        int[] scores = score.readScore(playerId, LiquibaseMigrationServiceTests.getConnection());
        assertEquals(15, scores[0], "Player Score sollte korrekt aktualisiert werden.");
    }

    @Test
    void shouldUpdateComputerScore() throws SQLException {
        int playerId = 3;
        score.write(playerId, 10, 5, 3, LiquibaseMigrationServiceTests.getConnection());
        score.writeComputerscore(playerId, 4, LiquibaseMigrationServiceTests.getConnection());

        int[] scores = score.readScore(playerId, LiquibaseMigrationServiceTests.getConnection());
        assertEquals(9, scores[1], "Computer Score sollte korrekt aktualisiert werden.");
    }

    @Test
    void shouldUpdateDrawScore() throws SQLException {
        int playerId = 4;
        score.write(playerId, 10, 5, 3, LiquibaseMigrationServiceTests.getConnection());
        score.writeDrawscore(playerId, 2, LiquibaseMigrationServiceTests.getConnection());

        int[] scores = score.readScore(playerId, LiquibaseMigrationServiceTests.getConnection());
        assertEquals(5, scores[2], "Draw Score sollte korrekt aktualisiert werden.");
    }

    @Test
    void shouldCheckIfPlayerScoreExists() throws SQLException {
        int playerId = 5;
        assertEquals(0, score.existsPlayerScore(playerId, LiquibaseMigrationServiceTests.getConnection()), "Score sollte noch nicht existieren.");

        score.write(playerId, 1, 1, 1, LiquibaseMigrationServiceTests.getConnection());
        assertEquals(1, score.existsPlayerScore(playerId, LiquibaseMigrationServiceTests.getConnection()), "Score sollte existieren.");
    }
}


