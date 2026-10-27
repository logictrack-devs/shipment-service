package ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion;

import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.UUID;

public class ExcepcionesCourier extends RuntimeException {

    public ExcepcionesCourier(String mensaje) {
        super(mensaje);
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public static class ErrorComunicacionCourierException extends RuntimeException {
        public ErrorComunicacionCourierException(String mensaje) {
            super(mensaje);
        }
    }

    // Excepción para cuando el envío no está PENDIENTE
    @ResponseStatus(HttpStatus.CONFLICT)
    public static class AsignacionNoPermitidaException extends RuntimeException {
        public AsignacionNoPermitidaException(UUID envioId, EstadoEnvio estado) {
            super("La asignación no está permitida. El envío " + envioId + " se encuentra en estado: " + estado);
        }
    }

    // Excepción por si el microservicio courier-service está caído o da error 500
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public static class ServicioNoDisponibleException extends RuntimeException {
        public ServicioNoDisponibleException(String mensaje) {
            super(mensaje);
        }
    }

    // Excepción para cuando el repartidor existe pero no está ACTIVO
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class RepartidorNoActivoException extends RuntimeException {

        // Constructor genérico para aceptar un solo String (Soluciona tu error actual)
        public RepartidorNoActivoException(String mensaje) {
            super(mensaje);
        }

        // Constructor detallado por si querés pasarle los datos limpios
        public RepartidorNoActivoException(UUID repartidorId, String estado) {
            super("El repartidor con ID " + repartidorId + " no puede ser asignado porque su estado es: " + estado);
        }
    }
}
