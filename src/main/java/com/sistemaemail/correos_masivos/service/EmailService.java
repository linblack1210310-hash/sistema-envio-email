package com.sistemaemail.correos_masivos.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${sendgrid.from}")
    private String remitente;

    @Value("${sendgrid.to}")
    private String destinatario;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void enviar(String nombre, String email, String mensaje) throws IOException {

        if (apiKey.isBlank() || remitente.isBlank() || destinatario.isBlank()) {
            throw new IOException("Faltan variables de entorno de SendGrid");
        }

        Map<String, Object> contenido = Map.of(
            "personalizations", List.of(
                Map.of(
                    "to", List.of(Map.of("email", destinatario)),
                    "subject", "Nuevo mensaje de " + nombre
                )
            ),
            "from", Map.of("email", remitente),
            "reply_to", Map.of("email", email),
            "content", List.of(
                Map.of(
                    "type", "text/plain",
                    "value", "Nombre: " + nombre
                        + "\nCorreo: " + email
                        + "\n\nMensaje:\n" + mensaje
                )
            )
        );

        String json = objectMapper.writeValueAsString(contenido);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.sendgrid.com/v3/mail/send"))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        try {
            HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IOException(
                    "Error de SendGrid. Código HTTP: " + response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("El envío del correo fue interrumpido", e);
        }
    }
}
