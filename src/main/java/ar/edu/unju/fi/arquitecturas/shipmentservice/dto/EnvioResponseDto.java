package ar.edu.unju.fi.arquitecturas.shipmentservice.dto;

import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioResponseDto {
    private UUID id;
    private String codigoSeguimiento;
    private Double peso;
    private Double ancho;
    private Double largo;
    private Double tarifa;
    private EstadoEnvio estado;
}
