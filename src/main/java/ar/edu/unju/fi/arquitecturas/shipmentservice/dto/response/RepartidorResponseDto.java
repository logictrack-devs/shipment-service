package ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response;


import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoRepartidor;
import lombok.*;
import java.util.UUID;

/**
 * DTO de respuesta para representar la informacion publica de un repartidor.
 *
 * <p>
 * Oculta detalles internos de la entidad JPA y expone unicamente
 * los atributos necesarios para la capa REST.
 * </p>
 *
 * @author Equipo LogicTrack Devs
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepartidorResponseDto {

    private UUID id;
    private String nombre;
    private String tipoVehiculo;
    private EstadoRepartidor estado;
}

