package ar.edu.unju.fi.arquitecturas.shipmentservice.controlador;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Direccion;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.CambioEstadoRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.HistorialEstadoResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.EnvioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/v1/envios")
@RequiredArgsConstructor
public class EnvioController {
    private final EnvioService envioService;
    @PostMapping
    public ResponseEntity<EnvioResponseDto> registrarEnvio(@Valid @RequestBody Double peso, Double largo, Double ancho, Cliente cliente, Direccion origen, Direccion destino){
        EnvioResponseDto nuevoEnvio= envioService.registrarEnvio(peso, largo, ancho, cliente, origen, destino);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEnvio);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvioResponseDto> getById(@PathVariable UUID id){
        EnvioResponseDto envio= envioService.getById(id);
        return ResponseEntity.ok(envio);
    }
/*
    @PatchMapping("/{id}/estado")
    public ResponseEntity<EnvioResponseDto> cambiarEstado(@PathVariable UUID id, @Valid @RequestBody CambioEstadoRequestDto request) {
        return ResponseEntity.ok(envioService.cambiarEstado(id, request));
    }
    */
    @GetMapping("/{codigoSeguimiento}")
    public ResponseEntity<EnvioResponseDto> getByCodigoSeguimiento(@PathVariable String codigoSeguimiento){
        EnvioResponseDto envio= envioService.getByCodigoSeguimiento(codigoSeguimiento);
        return ResponseEntity.ok(envio);
    }
/*
        @GetMapping("/{id}/historial")
        public ResponseEntity<List<HistorialEstadoResponseDto>> historial(@PathVariable UUID id) {
            return ResponseEntity.ok(envioService.obtenerHistorial(id));
        }
*/
    @GetMapping
    public ResponseEntity<List<EnvioResponseDto>> listAllEnvios(){
        List<EnvioResponseDto> envios= envioService.listAllEnvios();
        return ResponseEntity.ok(envios);
    }
}


