package ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request;


import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Direccion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para la creación de un nuevo envío mediante RequestBody.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioCreateRequestDto {

    @NotNull(message = "El ID del cliente es obligatorio")
    private UUID clienteId;

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser un número positivo")
    private Double peso;

    @NotNull(message = "El largo es obligatorio")
    @Positive(message = "El largo debe ser un número positivo")
    private Double largo;

    @NotNull(message = "El ancho es obligatorio")
    @Positive(message = "El ancho debe ser un número positivo")
    private Double ancho;

    @NotNull(message = "El alto es obligatorio")
    @Positive(message = "El alto debe ser un número positivo")
    private Double alto;

    @NotNull(message = "La dirección de origen es obligatoria")
    @Valid
    private Direccion origen;

    @NotNull(message = "La dirección de destino es obligatoria")
    @Valid
    private Direccion destino;
}

