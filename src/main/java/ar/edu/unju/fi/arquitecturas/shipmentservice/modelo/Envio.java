package ar.edu.unju.fi.arquitecturas.shipmentservice.modelo;

import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "envio")

public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "codigoSeguimiento", nullable = false, length = 50)
    private String codigoSeguimiento;

    @Column(name = "peso", nullable = false, scale = 2)
    private Double peso;

    @Column(name = "ancho", nullable = false, scale = 2)
    private Double ancho;

    @Column(name = "largo", nullable = false, scale = 2)
    private Double largo;

    @Column(name = "tarifa", nullable = false, scale = 2)
    private Double tarifa;

    @Column(name = "estado")
    private EstadoEnvio estadoEnvio;

}
