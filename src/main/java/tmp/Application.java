package tmp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


import com.sun.net.httpserver.HttpServer;


class Application {

    public static void main(String[] args) throws IOException {
        int serverPort = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/api/hello", (exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                //String respText = "Hello!";
                Map<String, List<String>> params = splitQuery(exchange.getRequestURI().getRawQuery());
                String noNameText = "Anonymous";
                String name = params.getOrDefault("name", List.of(noNameText)).stream().findFirst().orElse(noNameText);
                String respText = String.format("Hello %s!", name);
                exchange.sendResponseHeaders(200, respText.getBytes().length);
                OutputStream output = exchange.getResponseBody();
                output.write(respText.getBytes());
                output.flush();
            }else {
                exchange.sendResponseHeaders(405, -1);
            }

        }));
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    private static String decode(String value) {
        return value == null ? null : URLDecoder.decode(value, StandardCharsets.UTF_8);
    }


    public static Map<String, List<String>> splitQuery(String query) {
        if (query == null || "".equals(query)) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));
    }


}