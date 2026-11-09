package ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Controladora global de excepciones para la API REST del microservicio.
 * Estandariza la respuesta JSON con códigos de estado HTTP precisos y sin duplicados.
 */
@RestControllerAdvice
public class ManejadorExcepcionesGlobal {

    // 400: Validaciones de Bean Validation (@Valid en DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidaciones(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((m1, m2) -> m1 + "; " + m2)
                .orElse("Datos inválidos en la solicitud");
        return construirRespuesta(HttpStatus.BAD_REQUEST, mensaje);
    }

    // 400: Argumento ilegal / inválido
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return construirRespuesta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // 401: No autenticado / Token inválido
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(AuthenticationException ex) {
        return construirRespuesta(HttpStatus.UNAUTHORIZED, "Token inválido o autenticación requerida");
    }

    // 403: Permisos insuficientes (Acceso denegado a recursos ajenos o rol no autorizado)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleForbidden(AccessDeniedException ex) {
        return construirRespuesta(HttpStatus.FORBIDDEN, ex.getMessage() != null ? ex.getMessage() : "No tienes permisos para realizar esta acción");
    }

    // 404: Envío, cliente o recurso no encontrado
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNoEncontrado(RecursoNoEncontradoException ex) {
        return construirRespuesta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // 409: Conflictos de estado (No se puede cancelar, repartidor inactivo o asignación no permitida)
    @ExceptionHandler({
            Excepciones_envio.EnvioNoCancelableException.class,
            ExcepcionesCourier.AsignacionNoPermitidaException.class,
            ExcepcionesCourier.RepartidorNoActivoException.class
    })
    public ResponseEntity<Map<String, Object>> handleConflictos(RuntimeException ex) {
        return construirRespuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    // 422: Reglas de negocio no procesables (Transiciones inválidas de estado o repartidor no asignado)
    @ExceptionHandler({
            Excepciones_envio.TransicionEstadoInvalidaException.class,
            Excepciones_envio.RepartidorNoAsignadoException.class
    })
    public ResponseEntity<Map<String, Object>> handleReglaNegocio(RuntimeException ex) {
        return construirRespuesta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    // 503: Courier Service no disponible o error de comunicación
    @ExceptionHandler({
            ExcepcionesCourier.ErrorComunicacionCourierException.class,
            ExcepcionesCourier.ServicioNoDisponibleException.class
    })
    public ResponseEntity<Map<String, Object>> handleCourierCaido(RuntimeException ex) {
        return construirRespuesta(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    // 500: Fallback para cualquier error inesperado no controlado
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarRuntimeException(RuntimeException ex) {
        return construirRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor"
        );
    }

    // Método auxiliar para armar el formato JSON estándar
    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}