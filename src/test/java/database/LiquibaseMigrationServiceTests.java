package database;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LiquibaseMigrationServiceTests {


    private static Connection connection;


    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("testpass")
            .waitingFor(Wait.forListeningPort());


    public static String jdbcUrl;
    public static String username;
    public static String password;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        jdbcUrl = postgres.getJdbcUrl();
        username = postgres.getUsername();
        password = postgres.getPassword();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }



    public void runTestMigration(PostgreSQLContainer<?> postgres) {
        postgres.start();
        String JDBC_URL = postgres.getJdbcUrl();
        String USERNAME = postgres.getUsername();
        String PASSWORD = postgres.getPassword();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(), database);

            liquibase.update(new Contexts(), new LabelExpression());

            System.out.println("Datenbankmigration abgeschlossen.");
        } catch (SQLException | LiquibaseException e) {
            System.out.println("Testcontainers JDBC URL: " + postgres.getJdbcUrl());

            System.err.println("Fehler bei der Datenbankmigration: " + e.getMessage());
            e.printStackTrace();
        }
    }





    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            jdbcUrl = postgres.getJdbcUrl();
            username = postgres.getUsername();
            password = postgres.getPassword();
            connection = DriverManager.getConnection(jdbcUrl, username, password);
        }
        return connection;
    }


}