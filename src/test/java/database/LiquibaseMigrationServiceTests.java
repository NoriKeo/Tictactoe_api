package database;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LiquibaseMigrationServiceTests {


    public void runTestMigration(PostgreSQLContainer<?> postgres) {
        final String JDBC_URL = (postgres != null && postgres.isRunning())
                ? postgres.getJdbcUrl()
                : "jdbc:postgresql://localhost:5432/testdb";  // Standard-DB

        final String USERNAME = (postgres != null && postgres.isRunning())
                ? postgres.getUsername()
                : "postgres";

        final String PASSWORD = (postgres != null && postgres.isRunning())
                ? postgres.getPassword()
                : "testpass";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(), database);

            liquibase.update(new Contexts(), new LabelExpression());

            System.out.println("Datenbankmigration abgeschlossen.");
        } catch (SQLException | LiquibaseException e) {
            System.err.println("Fehler bei der Datenbankmigration: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
