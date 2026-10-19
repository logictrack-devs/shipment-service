package ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response;

import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HistorialEstadoResponseDto {
    private EstadoEnvio estadoAnterior;
    private EstadoEnvio estadoNuevo;
    private LocalDateTime fechaCambio;
    private String observacion;
}
