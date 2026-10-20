package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.impl;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.CambioEstadoRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.HistorialEstadoResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.Excepciones_envio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Direccion;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Envio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.HistorialEstadoEnvio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Repartidor;
import ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio.ClienteRepository;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;
    private final ClienteRepository clienteRepository;
    private final HistorialEstadoEnvioRepository historialRepository;

    @Override
    @Transactional
    public EnvioResponseDto registrarEnvio(Double peso, Double largo, Double ancho, Cliente cliente, Direccion origen, Direccion destino) {
        log.info("Registrando envío para el cliente: {}", cliente.getNombre());
        Cliente clienteEnvio = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado."));

        Envio nuevoEnvio = new Envio();
        nuevoEnvio.setAncho(ancho);
        nuevoEnvio.setLargo(largo);
        nuevoEnvio.setPeso(peso);
        nuevoEnvio.setCliente(clienteEnvio);
        nuevoEnvio.setEstado(EstadoEnvio.PENDIENTE);

        Envio envioGuardar = envioRepository.save(nuevoEnvio);
        return mapearResponseDto(envioGuardar);
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDto getById(UUID id) {
        log.info("Buscando envío con ID: {}", id);
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Envío no encontrado."));
        return mapearResponseDto(envio);
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDto getByCodigoSeguimiento(String codigoSeguimiento) {
        log.info("Buscando envío con Cod. Seguimiento: {}.", codigoSeguimiento);
        Envio envio = envioRepository.findByCodigoSeguimiento(codigoSeguimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Envío no encontrado."));
        return mapearResponseDto(envio);
    }

    @Override
    @Transactional
    public EnvioResponseDto updateEnvio(String codigoSeguimiento, EnvioRequestDto request) {
        Envio envio = envioRepository.findByCodigoSeguimiento(codigoSeguimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Envío no encontrado."));
        envio.setEstado(request.getEstado());
        Envio envioNuevo = envioRepository.save(envio);
        return mapearResponseDto(envioNuevo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDto> listAllEnvios() {
        return envioRepository.findAll().stream()
                .map(this::mapearResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDto> listByEstado(EstadoEnvio estado) {
        return envioRepository.findAll().stream()
                .map(this::mapearResponseDto)
                .filter(envio -> envio.getEstado() == estado)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
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
        return mapearResponseDto(envio);
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

    @Override
    @Transactional
    public void cancelarEnvio(String codigoSeguimiento) {
        log.info("Cancelando envío correspondiente al Cod. Seguimiento: {}.", codigoSeguimiento);
        Envio envio = envioRepository.findByCodigoSeguimiento(codigoSeguimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException("Envío no encontrado."));
        if (envio.getEstado() == EstadoEnvio.PENDIENTE) {
            EnvioRequestDto envioCancelar = new EnvioRequestDto(envio.getCodigoSeguimiento(), envio.getId(), EstadoEnvio.CANCELADO);
            updateEnvio(codigoSeguimiento, envioCancelar);
            log.info("El envío {} ha sido cancelado con éxito.", codigoSeguimiento);
        } else {
            log.error("El envío {} no pudo ser cancelado porque se encuentra en estado {}", codigoSeguimiento, envio.getEstado());
        }
    }

    private EnvioResponseDto mapearResponseDto(Envio envio) {
        EnvioResponseDto dto = new EnvioResponseDto();
        dto.setAncho(envio.getAncho());
        dto.setLargo(envio.getLargo());
        dto.setPeso(envio.getPeso());
        dto.setEstado(envio.getEstado());
        dto.setId(envio.getId());
        dto.setCodigoSeguimiento(envio.getCodigoSeguimiento());
        dto.setTarifa(envio.getTarifa());
        if (envio.getCliente() != null) {
            dto.setClienteId(envio.getCliente().getId());
            dto.setNombreCliente(envio.getCliente().getNombre());
        }
        return dto;
    }
}