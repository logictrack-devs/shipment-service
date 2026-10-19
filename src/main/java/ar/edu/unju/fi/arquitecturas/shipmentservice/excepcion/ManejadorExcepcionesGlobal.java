package ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
 *
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
    }

