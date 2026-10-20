package ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response;

import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Direccion;
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
    private Direccion origen;
    private Direccion destino;
    private UUID clienteId;
    private String nombreCliente;
}
