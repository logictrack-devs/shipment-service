package ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO para la solicitud de registro de un nuevo repartidor en el sistema.
 *
 * <p>
 * Contiene las validaciones minimas para garantizar que los datos
 * obligatorios sean proporcionados en la API REST.
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
public class RepartidorRequestDto {

    @NotBlank(message = "El nombre del repartidor es obligatorio.")
    private String nombre;

    @NotBlank(message = "El tipo de vehiculo es obligatorio.")
    private String tipoVehiculo;
}
