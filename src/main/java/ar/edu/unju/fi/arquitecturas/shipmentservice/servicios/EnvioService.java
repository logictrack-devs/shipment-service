package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Direccion;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.CambioEstadoRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.HistorialEstadoResponseDto;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;

public interface EnvioService {
    EnvioResponseDto registrarEnvio(Double peso, Double largo, Double ancho, Cliente cliente, Direccion origen, Direccion destino);
    EnvioResponseDto getById(UUID id);
    EnvioResponseDto getByCodigoSeguimiento(String codigoSeguimiento);
    EnvioResponseDto updateEnvio(String codigoSeguimiento, EnvioRequestDto request);
    List<EnvioResponseDto> listAllEnvios();
    //EnvioResponseDto createEnvio(EnvioRequestDto request);
    List<EnvioResponseDto> listByEstado(EstadoEnvio estado);
    //EnvioResponseDto cambiarEstado(UUID id, CambioEstadoRequestDto request);
    //EnvioResponseDto assignRepartidor(UUID envioId, UUID repartidorId);

    @Transactional(readOnly = true)
    //List<HistorialEstadoResponseDto> obtenerHistorial(UUID envioId);
    void cancelarEnvio(String codigoSeguimiento);
}
