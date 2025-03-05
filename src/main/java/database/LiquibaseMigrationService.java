package database;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LiquibaseMigrationService {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/tiktaktoe_database";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "mysecretpassword";

    public void runMigration() {
        try (Connection connection = openConnection()) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml", new ClassLoaderResourceAccessor(), database);

            liquibase.update(new Contexts(), new LabelExpression());

            System.out.println("Datenbankmigration abgeschlossen.");
        } catch (SQLException | liquibase.exception.LiquibaseException e) {
            System.err.println("Fehler bei der Datenbankmigration: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}

