package requesthandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import database.ConnectionHandler;
import database.LiquibaseMigrationServiceTests;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginHandlerTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("init.sql")
            .withDatabaseName("testdb")
            .withUsername("postgres")
            .withPassword("testpass");

    private LoginHandler loginHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        loginHandler = new LoginHandler();
        initializeDatabase();
    }

    private void initializeDatabase() throws SQLException {
        try (Connection connection = LiquibaseMigrationServiceTests.getConnection()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS accounts (
                    player_id SERIAL PRIMARY KEY,
                    player_name TEXT UNIQUE NOT NULL,
                    passwort TEXT NOT NULL
                );
            """;
            try (PreparedStatement stmt = connection.prepareStatement(createTableSQL)) {
                stmt.execute();
            }

            insertTestUser("testUser", "testPass");
        }
    }

    private void insertTestUser(String playerName, String password) throws SQLException {
        try (Connection connection = LiquibaseMigrationServiceTests.getConnection()) {
            String sql = "INSERT INTO accounts (player_name, passwort) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, playerName);
                stmt.setString(2, RequestUtil.hashPassword(password));
                stmt.execute();
            }
        }
    }

    @Test
    void testSuccessfulLogin() throws IOException {
        HttpExchange exchange = mockHttpExchange("{\"playerName\": \"testUser\", \"password\": \"testPass\"}");

        loginHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_OK), anyLong());
    }

    @Test
    void testFailedLogin() throws IOException {
        HttpExchange exchange = mockHttpExchange("{\"playerName\": \"wrongUser\", \"password\": \"wrongPass\"}");

        loginHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(HttpURLConnection.HTTP_OK), anyLong());
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
