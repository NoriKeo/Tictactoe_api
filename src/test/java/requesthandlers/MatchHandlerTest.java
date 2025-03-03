package requesthandlers;

import com.sun.net.httpserver.HttpExchange;
import controller.ServerController;
import database.*;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchHandlerTest {

    private MatchHandler matchHandler;

    private ServerController serverController;

    @Mock
    private HttpExchange exchange;

    @Mock
    private MatchReader matchReader;

    @Mock
    private MatchWrite matchWrite;

    @Mock
    private MoveReader moveReader;

    @Mock
    private MoveWriter moveWriter;

    @Mock
    private Score score;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        matchHandler = new MatchHandler();
        serverController = new ServerController();
    }

    @Test
    void testHandle_PostRequestWithValidData() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("POST");
        String jsonInput = new JSONObject()
                .put("playerId", 1)
                .put("move", 3)
                .toString();

        InputStream inputStream = new ByteArrayInputStream(jsonInput.getBytes());
        when(exchange.getRequestBody()).thenReturn(inputStream);

        OutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        matchHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(anyInt(), anyLong());
    }

    @Test
    void testHandle_InvalidMethod() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("GET");
        OutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);
        serverController.serverStart(8000);
        System.out.println("Server started");
        InitializeDatabase.initializeTables();
        serverController.endpoints();
        matchHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(eq(405), anyLong());

    }

    @Test
    void testHandle_InvalidJson() throws IOException {
        when(exchange.getRequestMethod()).thenReturn("POST");
        InputStream inputStream = new ByteArrayInputStream("invalid json".getBytes());
        when(exchange.getRequestBody()).thenReturn(inputStream);

        OutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        matchHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(anyInt(), anyLong());
    }
}
