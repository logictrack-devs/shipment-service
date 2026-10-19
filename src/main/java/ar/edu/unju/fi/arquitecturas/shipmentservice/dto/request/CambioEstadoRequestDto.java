package ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request;

import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CambioEstadoRequestDto {
    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoEnvio nuevoEstado;

    @Size(max = 200)
    private String observacion;
}