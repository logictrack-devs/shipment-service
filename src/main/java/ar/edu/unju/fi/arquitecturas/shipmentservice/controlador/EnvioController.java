package ar.edu.unju.fi.arquitecturas.shipmentservice.controlador;


import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.CambioEstadoRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.HistorialEstadoResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.EnvioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/envios")
    @RequiredArgsConstructor
    public class EnvioController {

        private final EnvioService envioService;

        @PatchMapping("/{id}/estado")
        public ResponseEntity<EnvioResponseDto> cambiarEstado(@PathVariable UUID id, @Valid @RequestBody CambioEstadoRequestDto request) {
            return ResponseEntity.ok(envioService.cambiarEstado(id, request));
        }

        @GetMapping("/{id}/historial")
        public ResponseEntity<List<HistorialEstadoResponseDto>> historial(@PathVariable UUID id) {
            return ResponseEntity.ok(envioService.obtenerHistorial(id));
        }
    }


