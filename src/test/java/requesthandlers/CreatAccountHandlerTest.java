package requesthandlers;

import com.sun.net.httpserver.HttpExchange;
import controller.ServerController;
import database.InitializeDatabase;
import database.LiquibaseMigrationServiceTests;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class CreatAccountHandlerTest {



    CreatAccountHandler createAccountHandler;
    private HttpExchange exchange;
    private ServerController serverController;


    @BeforeEach
    void setUp() throws SQLException, InterruptedException {

        createAccountHandler = new CreatAccountHandler();
        exchange = mock(HttpExchange.class);
        serverController = new ServerController();
        Thread.sleep(10000);
        LiquibaseMigrationServiceTests liquibaseMigrationService = new LiquibaseMigrationServiceTests();
        liquibaseMigrationService.runTestMigration(LiquibaseMigrationServiceTests.postgres);

    }



    @Test
    void testCreateAccount_Success() throws SQLException {
        String playerName = "TestPlayer";
        String password = "password123";
        String securityAnswer = "Answer123";

        int rowsAffected = createAccountHandler.createAccount(playerName, password, securityAnswer,LiquibaseMigrationServiceTests.getConnection());
        assertEquals(1, rowsAffected, "Account sollte erfolgreich erstellt worden sein.");
        String query = "SELECT * FROM accounts WHERE player_name = ?";
        try (Connection connection = LiquibaseMigrationServiceTests.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerName);
            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Account-Daten sollten in der Datenbank existieren.");
                assertEquals(playerName, rs.getString("player_name"), "Spielername stimmt nicht überein.");
            }
        }
    }


    @Test
    void testCreateAccount_Failure_InvalidData() throws SQLException {
        String playerName = "";
        String password = "password123";
        String securityAnswer = "Answer123";

        int rowsAffected = createAccountHandler.createAccount(playerName, password, securityAnswer,LiquibaseMigrationServiceTests.getConnection());

        assertEquals(0, rowsAffected, "Account sollte aufgrund ungültiger Daten nicht erstellt werden.");
    }

    @Test
    void testHandler() throws IOException {
        HttpExchange exchange = mockHttpExchange("{\"playerName\": \"testUser\", \"password\": \"testPass\", \"securityAnswer\": \"testPass\"}");

        createAccountHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_OK), anyLong());
    }

    @Test
    void testCreateAccount_Failure_InvalidRequestMethod() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("GET");
        OutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);
        serverController.serverStart(8000);
        System.out.println("Server started");
        InitializeDatabase.initializeTables();
        serverController.endpoints();
        createAccountHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(405), anyLong());
    }

    private HttpExchange mockHttpExchange(String requestBody) throws IOException {
        HttpExchange exchange = mock(HttpExchange.class);
        InputStream inputStream = new ByteArrayInputStream(requestBody.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        when(exchange.getRequestMethod()).thenReturn("POST");
        when(exchange.getRequestBody()).thenReturn(inputStream);
        when(exchange.getResponseBody()).thenReturn(outputStream);

        return exchange;
    }


}