package ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion;

import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;

import java.util.UUID;

public class Excepciones_envio {
    public static class TransicionEstadoInvalidaException extends RuntimeException {
        public TransicionEstadoInvalidaException(EstadoEnvio desde, EstadoEnvio hacia) {
            super("Transición no permitida: " + desde + " → " + hacia);
        }
    }

    public static class RepartidorNoAsignadoException extends RuntimeException {
        public RepartidorNoAsignadoException(UUID envioId) {
            super("El envío " + envioId + " necesita un repartidor activo antes de pasar a EN_RUTA");
        }
    }
}
