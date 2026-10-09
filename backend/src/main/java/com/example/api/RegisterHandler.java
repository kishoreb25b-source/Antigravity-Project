package com.example.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.service.RegistrationService;
import com.example.service.RegistrationService.RegistrationResult;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RegisterHandler implements HttpHandler {

    private final RegistrationService registrationService;

    public RegisterHandler() {
        this(new RegistrationService());
    }

    public RegisterHandler(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // 1. Add CORS headers so frontend can communicate across ports
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        // 2. Handle CORS preflight OPTIONS request
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // 3. Only accept POST method
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendJsonResponse(exchange, 405, "{\"success\":false,\"message\":\"Method Not Allowed\"}");
            return;
        }

        // 4. Read request body
        InputStream is = exchange.getRequestBody();
        String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        // 5. Extract fields from JSON using plain standard Java (no external JSON libraries)
        String name = extractJsonField(requestBody, "name");
        String email = extractJsonField(requestBody, "email");
        String phone = extractJsonField(requestBody, "phone");
        String password = extractJsonField(requestBody, "password");

        // 6. Execute registration service
        RegistrationResult result = registrationService.register(name, email, phone, password);

        // 7. Send JSON response
        int statusCode = result.isSuccess() ? 201 : 400;
        String jsonResponse = String.format(
                "{\"success\":%b,\"message\":\"%s\"}",
                result.isSuccess(),
                escapeJson(result.getMessage())
        );

        sendJsonResponse(exchange, statusCode, jsonResponse);
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
        byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // Lightweight standard Java JSON field extractor
    private String extractJsonField(String json, String field) {
        Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).replace("\\\"", "\"").replace("\\\\", "\\");
        }
        return null;
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
