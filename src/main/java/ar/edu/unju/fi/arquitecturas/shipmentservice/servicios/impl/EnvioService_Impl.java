package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.impl;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.CambioEstadoRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.HistorialEstadoResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.Excepciones_envio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Envio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.HistorialEstadoEnvio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Repartidor;
import ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio.EnvioRepository;
import ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio.HistorialEstadoEnvioRepository;
import ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.EnvioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public  class EnvioService_Impl implements EnvioService {

    private final EnvioRepository envioRepository;
    private final HistorialEstadoEnvioRepository historialRepository;


    /// @param id
    /// @return
    public EnvioResponseDto getById(UUID id) {
        return null;
    }

    /// @param codigoSeguimiento
    /// @return
    public EnvioResponseDto getByCodigoSeguimiento(String codigoSeguimiento) {
        return null;
    }

    /// @param codigoSeguimiento
    /// @param request
    /// @return
    public EnvioResponseDto updateEnvio(String codigoSeguimiento, EnvioRequestDto request) {
        return null;
    }

    /// @return
    public List<EnvioResponseDto> listAllEnvios() {
        return List.of();
    }

    /// @param request
    /// @return
    public EnvioResponseDto createEnvio(EnvioRequestDto request) {
        return null;
    }

    /// @param estado
    /// @return
    public List<EnvioResponseDto> listByEstado(EstadoEnvio estado) {
        return List.of();
    }

    /// @param id
    /// @return
    public EnvioResponseDto cancelEnvio(UUID id) {
        return null;
    }

    @Override
    @Transactional // cambio de estado + historial: todo o nada
    public EnvioResponseDto cambiarEstado(UUID envioId, CambioEstadoRequestDto request) {
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el envío con ID: " + envioId));

        EstadoEnvio actual = envio.getEstado();
        EstadoEnvio destino = request.getNuevoEstado();

        if (!actual.puedeTransicionarA(destino)) {
            log.warn("Transición rechazada para envío {}: {} -> {}", envioId, actual, destino);
            throw new Excepciones_envio.TransicionEstadoInvalidaException(actual, destino);
        }

        if (destino == EstadoEnvio.EN_RUTA) {
            Repartidor repartidor = envio.getRepartidor();
            if (repartidor == null || repartidor.getEstado() != EstadoRepartidor.ACTIVO) {
                throw new Excepciones_envio.RepartidorNoAsignadoException(envioId);
            }
        }

        envio.setEstado(destino);
        envioRepository.save(envio);

        historialRepository.save(HistorialEstadoEnvio.builder()
                .envio(envio)
                .estadoAnterior(actual)
                .estadoNuevo(destino)
                .fechaCambio(LocalDateTime.now())
                .observacion(request.getObservacion())
                .build());

        log.info("Envío {} cambió de {} a {}", envioId, actual, destino);
        return mapearAResponseDto(envio);
    }

    /// @param envioId
    /// @param repartidorId
    /// @return
    public EnvioResponseDto assignRepartidor(UUID envioId, UUID repartidorId) {
        return null;
    }

    private EnvioResponseDto mapearAResponseDto(Envio envio) {
            Repartidor repartidor = envio.getRepartidor();

            return EnvioResponseDto.builder()
                    .id(envio.getId())
                    .codigoSeguimiento(envio.getCodigoSeguimiento())
                    .peso(envio.getPeso())
                    .ancho(envio.getAncho())
                    .largo(envio.getLargo())
                    .tarifa(envio.getTarifa())
                    .estado(envio.getEstado())
                    .id(envio.getCliente().getId())
                    .build();
        }




    @Override
    @Transactional(readOnly = true)
    public List<HistorialEstadoResponseDto> obtenerHistorial(UUID envioId) {
        if (!envioRepository.existsById(envioId)) {
            throw new RecursoNoEncontradoException("No existe el envío con ID: " + envioId);
        }
        return historialRepository.findByEnvioIdOrderByFechaCambioAsc(envioId).stream()
                .map(h -> HistorialEstadoResponseDto.builder()
                        .estadoAnterior(h.getEstadoAnterior())
                        .estadoNuevo(h.getEstadoNuevo())
                        .fechaCambio(h.getFechaCambio())
                        .observacion(h.getObservacion())
                        .build())
                .toList();
    }
}
