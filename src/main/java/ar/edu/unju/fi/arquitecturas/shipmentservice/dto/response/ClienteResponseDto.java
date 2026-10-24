package ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response;


import java.util.UUID;

/**
 * Datos de un cliente que se devuelven por la API.
 */
public record ClienteResponseDto(
        UUID id,
        String nombre,
        String email,
        String telefono) {
}