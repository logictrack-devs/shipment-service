package ar.edu.unju.fi.arquitecturas.shipmentservice.controlador;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.CambioEstadoRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.HistorialEstadoResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.EnvioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para exponer los endpoints de gestión de envíos.
 *
 * @author Equipo LogicTrack Devs
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    /**
     * Registra un nuevo envío en la plataforma.
     *
     * @param clienteId Identificador UUID del cliente solicitante.
     * @param peso Peso del paquete.
     * @param largo Largo del paquete.
     * @param ancho Ancho del paquete.
     * @return DTO del envío creado con código HTTP 201 Created.
     */
    @PostMapping
    public ResponseEntity<EnvioResponseDto> registrarEnvio(
            @RequestParam UUID clienteId,
            @RequestParam Double peso,
            @RequestParam Double largo,
            @RequestParam Double ancho,
            @RequestParam Double alto) { // <--- Swagger leerá esta anotación y creará la casilla para el alto
        Cliente cliente = new Cliente();
        cliente.setId(clienteId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(envioService.registrarEnvio(peso, largo, ancho, alto, cliente, null, null));
    }

    /**
     * Obtiene los detalles de un envío por su identificador único.
     *
     * @param id UUID del envío.
     * @return DTO con la información del envío.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponseDto> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(envioService.getById(id));
    }

    /**
     * Cambia el estado de un envío (PENDIENTE -> EN_RUTA -> ENTREGADO / CANCELADO).
     */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EnvioResponseDto> cambiarEstado(@PathVariable UUID id, @Valid @RequestBody CambioEstadoRequestDto request) {
        return ResponseEntity.ok(envioService.cambiarEstado(id, request));
    }

    /**
     * Consulta el historial de cambios de estado de un envío.
     */
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialEstadoResponseDto>> historial(@PathVariable UUID id) {
        return ResponseEntity.ok(envioService.obtenerHistorial(id));
    }
    /**
     * 👈 ENDPOINT NUEVO: Permite asignar un repartidor desde la API de Envíos.
     * Atiende exactamente la URL solicitada por Mica:
     * PATCH /api/v1/envios/{envioId}/asignar-repartidor/{repartidorId}
     */
    @PatchMapping("/{envioId}/asignar-repartidor/{repartidorId}")
    public ResponseEntity<EnvioResponseDto> asignarRepartidor(
            @PathVariable UUID envioId,
            @PathVariable UUID repartidorId) {

        log.info("API: Recibida petición para asignar el repartidor {} al envío {}", repartidorId, envioId);
        EnvioResponseDto response = envioService.asignarRepartidor(envioId, repartidorId);
        return ResponseEntity.ok(response);
    }
}