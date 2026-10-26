package com.sistemaemail.correos_masivos.controller;

import com.sistemaemail.correos_masivos.dto.MensajeRequest;
import com.sistemaemail.correos_masivos.service.EmailService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/mensajes")
public class MensajesController {

    private final EmailService emailService;

    public MensajesController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> estado() {
        return ResponseEntity.ok(
            Map.of("mensaje", "API de mensajería funcionando correctamente"));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> enviar(
            @Valid @RequestBody MensajeRequest request) {
        try {
            emailService.enviar(
                request.nombre().trim(),
                request.email().trim(),
                request.mensaje().trim());

            return ResponseEntity.ok(Map.of("mensaje", "Correo enviado correctamente"));

        } catch (IOException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "No fue posible enviar el correo"));
        }
    }
}
