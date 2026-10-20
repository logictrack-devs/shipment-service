package ar.edu.unju.fi.arquitecturas.shipmentservice.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

/**
 * Entidad JPA que representa a un repartidor en la plataforma de envíos.
 * <p>
 * Mapea la tabla {@code repartidores} en la base de datos relacional y almacena
 * los datos de identificación y estado del repartidor.
 * </p>
 *
 * @author Equipo LogicTrack Devs
 * @version 1.0
 */
@Entity
@Table(name = "repartidores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Repartidor extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nombre", nullable = false, length=20)
    private String nombre;

    @Column(name = "tipo_vehiculo", nullable = false, length=50)
    private String tipoVehiculo;
         
   
    @Enumerated(EnumType.STRING)
    @Column(name="estadorepartidor",nullable=false)
    @Builder.Default
    private EstadoRepartidor estado=EstadoRepartidor.ACTIVO;
}