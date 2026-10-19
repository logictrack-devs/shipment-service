package ar.edu.unju.fi.arquitecturas.shipmentservice.modelo;

import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
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


    private EstadoEnvio estadoEnvio;
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    @Builder.Default
    private EstadoEnvio estado = EstadoEnvio.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clienteId", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "repartidorId", nullable = false)
    private Repartidor repartidor;

 public boolean puedeCancelarse() {
    return estado == EstadoEnvio.PENDIENTE;
}
}
