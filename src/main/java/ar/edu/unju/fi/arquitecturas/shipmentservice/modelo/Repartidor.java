package ar.edu.unju.fi.arquitecturas.shipmentservice.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoRepartidor;


@Entity
@Table(name = "repartidores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Repartidor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nombre", nullable = false, length=20)
    private String nombre;

    @Column(name = "tipo_vehiculo", nullable = false, length=50)
    private String tipoVehiculo;

    @Column(name="estadorepartidor")
    private EstadoRepartidor estado;
}