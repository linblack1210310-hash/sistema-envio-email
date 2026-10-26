package com.sistemaemail.correos_masivos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MensajeRequest(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre es demasiado largo")
    String nombre,

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debes ingresar un correo válido")
    @Size(max = 254)
    String email,

    @NotBlank(message = "El mensaje es obligatorio")
    @Size(max = 5000, message = "El mensaje es demasiado largo")
    String mensaje

) {
}
