package ar.edu.unju.fi.arquitecturas.shipmentservice.modelo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Value Object (Objeto de Valor) que representa una dirección geográfica en el sistema.
 * <p>
 * Al utilizar la anotación {@link Embeddable}, esta clase no constituye una entidad persistente
 * independiente ni posee una tabla o identificador primario en la base de datos.
 * Sus atributos son incrustados directamente dentro de las entidades que la contengan
 * (por ejemplo, en la entidad {@code Envio} para mapear el punto de origen y de destino).
 * </p>
 *
 * @author Equipo LogicTrack Devs
 * @version 1.0
 */

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {

    private String calle;
    private String numero;
    private String ciudad;
    private String codigoPostal;
}