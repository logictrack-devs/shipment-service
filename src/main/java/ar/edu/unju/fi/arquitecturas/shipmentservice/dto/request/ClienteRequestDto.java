package ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos que se reciben para registrar o actualizar un cliente.
 */
public record ClienteRequestDto(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 100, message = "El email no puede superar los 100 caracteres")
        String email,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
        String telefono) {
}

