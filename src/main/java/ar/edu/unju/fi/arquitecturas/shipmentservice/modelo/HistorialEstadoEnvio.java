package ar.edu.unju.fi.arquitecturas.shipmentservice.modelo;



import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

    @Entity
    @Table(name = "historial_estado_envio")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class HistorialEstadoEnvio extends Auditable {

        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @JdbcTypeCode(SqlTypes.CHAR)
        @Column(length = 36, updatable = false, nullable = false)
        private UUID id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "envio_id", nullable = false, updatable = false)
        private Envio envio;

        @Enumerated(EnumType.STRING)
        @Column(name = "estado_anterior", length = 15, updatable = false) // null en el primer registro
        private EstadoEnvio estadoAnterior;

        @Enumerated(EnumType.STRING)
        @Column(name = "estado_nuevo", nullable = false, length = 15, updatable = false)
        private EstadoEnvio estadoNuevo;

        @Column(name = "fecha_cambio", nullable = false, updatable = false)
        private LocalDateTime fechaCambio;

        @Column(name = "observacion", length = 255, updatable = false)
        private String observacion;
    }

