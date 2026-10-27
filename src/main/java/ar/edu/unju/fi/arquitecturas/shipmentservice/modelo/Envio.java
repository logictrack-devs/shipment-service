package ar.edu.unju.fi.arquitecturas.shipmentservice.modelo;

import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "envio")
public class Envio extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "codigoSeguimiento", nullable = false, length = 50)
    private String codigoSeguimiento;

    @Column(name = "peso", nullable = false, scale = 2)
    private Double peso;

    @Column(name = "ancho", nullable = false, scale = 2)
    private Double ancho;

    @Column(name = "alto", nullable = false, scale = 2)
    private Double alto;

    @Column(name = "largo", nullable = false, scale = 2)
    private Double largo;

    @Column(name = "tarifa", nullable = false, scale = 2)
    private Double tarifa;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoEnvio estado = EstadoEnvio.PENDIENTE;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "origen_calle")),
            @AttributeOverride(name = "numero", column = @Column(name = "origen_numero")),
            @AttributeOverride(name = "ciudad", column = @Column(name = "origen_ciudad")),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "origen_codigo_postal"))
    })
    private Direccion direccionOrigen;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "destino_calle")),
            @AttributeOverride(name = "numero", column = @Column(name = "destino_numero")),
            @AttributeOverride(name = "ciudad", column = @Column(name = "destino_ciudad")),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "destino_codigo_postal"))
    })
    private Direccion direccionDestino;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // REEMPLAZÁ LA RELACIÓN ANTERIOR POR ESTA COLUMNA SIMPLE:
    @Column(name = "repartidor_id", nullable = true)
    private UUID repartidorId;

    public boolean puedeCancelarse() {
        return estado == EstadoEnvio.PENDIENTE;
    }
}