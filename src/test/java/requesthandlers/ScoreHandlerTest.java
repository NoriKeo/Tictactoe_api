package requesthandlers;

import com.sun.net.httpserver.HttpExchange;
import database.ConnectionHandler;
import database.LiquibaseMigrationServiceTests;
import database.MatchReader;
import database.Score;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScoreHandlerTest {



        private ScoreHandler scoreHandler;
        private HttpExchange exchangeMock;
        private MatchReader matchReaderMock;
        private Score scoreMock;
        Connection connection;

    {
        try {
            connection = LiquibaseMigrationServiceTests.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
        void setUp() throws IOException {
            scoreHandler = new ScoreHandler();
            exchangeMock = mock(HttpExchange.class);
            matchReaderMock = mock(MatchReader.class);
            scoreMock = mock(Score.class);

            JSONObject requestBody = new JSONObject();
            requestBody.put("playerId", 123);
            InputStream requestStream = new ByteArrayInputStream(requestBody.toString().getBytes(StandardCharsets.UTF_8));

            when(exchangeMock.getRequestMethod()).thenReturn("POST");
            when(exchangeMock.getRequestBody()).thenReturn(requestStream);
        }

        @Test
        void testHandle_ValidPlayerId_ReturnsScore() throws IOException, SQLException {
            MatchReader matchReaderMock = mock(MatchReader.class);
            when(matchReaderMock.matchStatus(123, 4,connection)).thenReturn(1);

            Score scoreMock = mock(Score.class);
            Connection connection = LiquibaseMigrationServiceTests.getConnection();
            when(scoreMock.readScore(123,connection)).thenReturn(new int[]{5, 3, 2});

            OutputStream outputStream = new ByteArrayOutputStream();
            when(exchangeMock.getResponseBody()).thenReturn(outputStream);

            scoreHandler.handle(exchangeMock);

            String response = outputStream.toString();
            assertEquals("playerscore 5 computerScore 3 drawScore 2", response.trim());
        }

        @Test
        void testHandle_NoMatchFound_ReturnsErrorMessage() throws IOException {
            when(matchReaderMock.matchStatus(123, 4,connection)).thenReturn(-1);

            OutputStream outputStream = new ByteArrayOutputStream();
            when(exchangeMock.getResponseBody()).thenReturn(outputStream);

            scoreHandler.handle(exchangeMock);

            String response = outputStream.toString();
            assertEquals("für die playerid 123 ligt kein score vor", response.trim());
        }

        @Test
        void testHandle_InvalidRequestMethod_Returns405() throws IOException {
            when(exchangeMock.getRequestMethod()).thenReturn("GET");

            OutputStream outputStream = new ByteArrayOutputStream();
            when(exchangeMock.getResponseBody()).thenReturn(outputStream);

            scoreHandler.handle(exchangeMock);

            verify(exchangeMock).sendResponseHeaders(405, 0);
        }


}