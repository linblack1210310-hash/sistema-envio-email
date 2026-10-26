package com.sistemaemail.correos_masivos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class EmailService {

    @Value("${cloudflare.worker.url}")
    private String workerUrl;

    @Value("${cloudflare.worker.token}")
    private String workerToken;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void enviar(String nombre, String email, String mensaje) throws IOException {

        if (workerUrl == null || workerUrl.isBlank() || workerToken == null || workerToken.isBlank()) {
            throw new IOException("Faltan las credenciales del Worker de Cloudflare en application.properties");
        }

        // Estructura que espera tu Cloudflare Worker
        Map<String, Object> payload = Map.of(
            "nombre", nombre,
            "email", email,
            "mensaje", mensaje
        );

        String json = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(workerUrl))
            .header("X-Api-Token", workerToken)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        try {
            HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IOException(
                    "Error en Cloudflare Worker. Código HTTP: " + response.statusCode() + " - " + response.body());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("El envío del correo fue interrumpido", e);
        }
    }
}