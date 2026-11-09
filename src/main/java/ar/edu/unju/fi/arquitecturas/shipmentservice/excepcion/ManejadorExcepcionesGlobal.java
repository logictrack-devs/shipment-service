package ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion;
import org.springframework.security.core.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controladora global de excepciones para la API REST del microservicio.
 * <p>
 * Utiliza {@link RestControllerAdvice} para interceptar las excepciones no controladas
 * lanzadas en la capa de controlador o servicios, estandarizando la respuesta JSON
 * de error con información de auditoría y códigos de estado HTTP pertinentes.
 * </p>

 * @author Equipo LogicTrack Devs
 * @version 1.0
 */
@RestControllerAdvice
public class ManejadorExcepcionesGlobal {

    /**
     * Intercepta las excepciones de tipo {@link RuntimeException} no capturadas previamente.
     *
     * @param ex Excepción capturada durante la ejecución de la solicitud.
     * @return Una respuesta {@link ResponseEntity} estructurada con formato JSON y código HTTP 400 (Bad Request).
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "error", ex.getMessage(),
                "status", HttpStatus.BAD_REQUEST.value()
        ));
    }

    @ExceptionHandler({Excepciones_envio.TransicionEstadoInvalidaException.class, Excepciones_envio.RepartidorNoAsignadoException.class})
    public ResponseEntity<Map<String, Object>> handleReglaNegocio(RuntimeException ex) {
        return construirRespuesta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage()); // 422
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidaciones(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((m1, m2) -> m1 + "; " + m2)
                .orElse("Datos inválidos en la solicitud");
        return construirRespuesta(HttpStatus.BAD_REQUEST, mensaje);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 404: Envío o recurso no encontrado
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrado(RecursoNoEncontradoException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    // 409: Conflictos de estado (No está PENDIENTE, repartidor inactivo, etc.)
    @ExceptionHandler({
            Excepciones_envio.TransicionEstadoInvalidaException.class,
            Excepciones_envio.EnvioNoCancelableException.class,
            ExcepcionesCourier.AsignacionNoPermitidaException.class,
            ExcepcionesCourier.RepartidorNoActivoException.class
    })
    public ResponseEntity<Map<String, Object>> handleConflictos(RuntimeException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }
    // 403: Permisos insuficientes (Rol no autorizado)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(AccessDeniedException ex) {
        return construirRespuesta(HttpStatus.FORBIDDEN, "No tienes permisos suficientes para realizar esta acción");
    }
    // 401: No autenticado
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(AuthenticationException ex) {
        return construirRespuesta(HttpStatus.UNAUTHORIZED, "Token inválido o autenticación requerida");
    }
    // 503: Courier service no responde




    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Not Found",
                "mensaje", ex.getMessage()
        ));
    }
    @ExceptionHandler({ExcepcionesCourier.RepartidorNoActivoException.class,
            ExcepcionesCourier.AsignacionNoPermitidaException.class})
    public ResponseEntity<Map<String, Object>> handleConflicto(RuntimeException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }
    @ExceptionHandler({
            ExcepcionesCourier.ErrorComunicacionCourierException.class,
            ExcepcionesCourier.ServicioNoDisponibleException.class
    })
    public ResponseEntity<Map<String, Object>> handleCourierCaido(RuntimeException ex) {
        return construirRespuesta(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }


}


