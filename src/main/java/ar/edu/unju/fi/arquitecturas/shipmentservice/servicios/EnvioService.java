package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.EnvioResponseDto;

import java.util.List;
import java.util.UUID;

public interface EnvioService {
    EnvioResponseDto getById(UUID id);
    EnvioResponseDto getByCodigoSeguimiento(String codigoSeguimiento);
    EnvioResponseDto updateEnvio(String codigoSeguimiento, EnvioRequestDto request);
    List<EnvioResponseDto> listAllEnvios();
}
