package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.CambioEstadoRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.HistorialEstadoResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;

public interface EnvioService {
    EnvioResponseDto getById(UUID id);
    EnvioResponseDto getByCodigoSeguimiento(String codigoSeguimiento);
    EnvioResponseDto updateEnvio(String codigoSeguimiento, EnvioRequestDto request);
    List<EnvioResponseDto> listAllEnvios();
    EnvioResponseDto createEnvio(EnvioRequestDto request);
    List<EnvioResponseDto> listByEstado(EstadoEnvio estado);
    EnvioResponseDto cancelEnvio(UUID id);
    EnvioResponseDto cambiarEstado(UUID id, CambioEstadoRequestDto request);
    EnvioResponseDto assignRepartidor(UUID envioId, UUID repartidorId);

    @Transactional(readOnly = true)
    List<HistorialEstadoResponseDto> obtenerHistorial(UUID envioId);
}
