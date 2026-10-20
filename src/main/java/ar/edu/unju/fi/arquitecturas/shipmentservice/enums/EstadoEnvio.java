package ar.edu.unju.fi.arquitecturas.shipmentservice.enums;

import java.util.EnumSet;
import java.util.Set;

public enum EstadoEnvio {
    PENDIENTE, EN_RUTA, ENTREGADO, CANCELADO;

    public Set<EstadoEnvio> transicionesPermitidas() {
        return switch (this) {
            case PENDIENTE -> EnumSet.of(EN_RUTA, CANCELADO);
            case EN_RUTA   -> EnumSet.of(ENTREGADO);
            case ENTREGADO, CANCELADO -> EnumSet.noneOf(EstadoEnvio.class); // estados finales
        };
    }

    public boolean puedeTransicionarA(EstadoEnvio destino) {
        return transicionesPermitidas().contains(destino);
    }




}

