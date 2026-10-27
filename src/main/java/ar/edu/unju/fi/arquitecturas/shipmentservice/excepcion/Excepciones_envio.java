package ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion;

import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

public class Excepciones_envio {


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class TransicionEstadoInvalidaException extends RuntimeException {
        public TransicionEstadoInvalidaException(EstadoEnvio actual, EstadoEnvio destino) {
            super("No se puede transicionar el envío desde el estado " + actual + " hacia " + destino);
        }
    }

    // Excepción para cuando se pasa a EN_RUTA sin un chofer asignado
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class RepartidorNoAsignadoException extends RuntimeException {
        public RepartidorNoAsignadoException(java.util.UUID envioId) {
            super("No se puede poner en ruta el envío con ID: " + envioId + " porque no tiene un repartidor asignado.");
        }
}
}