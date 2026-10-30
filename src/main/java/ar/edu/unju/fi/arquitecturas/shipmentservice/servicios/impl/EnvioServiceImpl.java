package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.impl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest; // (Si usás Spring Boot 2.x, cambialo a javax.servlet.http.HttpServletRequest)
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.RepartidorExternoDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.CambioEstadoRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.HistorialEstadoResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.Excepciones_envio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.ExcepcionesCourier;
import ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Direccion;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Envio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.HistorialEstadoEnvio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio.EnvioRepository;
import ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio.HistorialEstadoEnvioRepository;
import ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.EnvioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación de los servicios de negocio vinculados a la gestión de envíos.
 * Los repartidores se consultan y validan en courier-service mediante {@link RestTemplate}.
 *
 * @author Equipo LogicTrack Devs
 * @version 2.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;
    private final ClienteRepository clienteRepository;
    private final HistorialEstadoEnvioRepository historialRepository;
    private final RestTemplate restTemplate;// Inyección de tu RestTemplate nativo de Spring
    @Value("${courier.service.url}")
    private String courierServiceUrl;

    @Override
    @Transactional
    public EnvioResponseDto registrarEnvio(Double peso, Double largo, Double ancho, Double alto, Cliente cliente, Direccion origen, Direccion destino) {
        log.info("Registrando envío para el cliente con ID: {}", cliente.getId());
        Cliente clienteEnvio = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + cliente.getId()));

        String codigoSeguimiento = "ENV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();


        // Se usa la alternativa segura de pasar el proxy directo de Hibernate o usar setter por fuera
        Envio nuevoEnvio = Envio.builder()
                .codigoSeguimiento(codigoSeguimiento)
                .peso(peso)
                .largo(largo)
                .ancho(ancho)
                .alto(alto)
                .estado(EstadoEnvio.PENDIENTE)
                .direccionOrigen(origen)
                .direccionDestino(destino)
                .build();

        nuevoEnvio.setCliente(clienteEnvio); // Evita problemas de Lombok Builder con herencia

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
        log.info("Buscando envío con Cod. Seguimiento: {}", codigoSeguimiento);
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
            UUID repartidorId = envio.getRepartidorId(); // Usa tu columna simple
            if (repartidorId == null) {
                throw new Excepciones_envio.RepartidorNoAsignadoException(envioId);
            }
            validarRepartidorActivo(repartidorId);
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

    /**
     * Asigna un repartidor externo al envío guardando únicamente su UUID de referencia.
     */
    @Override
    @Transactional
    public EnvioResponseDto asignarRepartidor(UUID envioId, UUID repartidorId) {
        log.info("Asignando el repartidor simple {} al envío {}", repartidorId, envioId);

        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el envío con ID: " + envioId));

        validarRepartidorActivo(repartidorId);

        envio.setRepartidorId(repartidorId); // Se persiste en tu columna de base de datos
        Envio envioGuardado = envioRepository.save(envio);

        log.info("Columna 'repartidor_id' actualizada con éxito para el envío {}", envioId);
        return mapearResponseDto(envioGuardado);
    }

    /**
     * Valida mediante RestTemplate si el repartidor existe y se encuentra ACTIVO en courier-service.
     */

    private void validarRepartidorActivo(UUID repartidorId) {
        // Usamos la variable que definiste arriba en lugar de localhost
        String url = courierServiceUrl + "/api/v1/repartidores/" + repartidorId;

        try {
            log.info("Consultando estado del repartidor {} vía RestTemplate", repartidorId);

            // 1. Extraemos el Token JWT de la petición que hizo Postman
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("Authorization");

            // 2. Armamos las cabeceras (Headers) y le inyectamos el token
            HttpHeaders headers = new HttpHeaders();
            if (token != null) {
                headers.set("Authorization", token);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // 3. Hacemos la petición usando exchange() para poder mandar las cabeceras
            ResponseEntity<RepartidorExternoDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    RepartidorExternoDto.class
            );

            RepartidorExternoDto repartidor = response.getBody();

            if (repartidor == null || !"ACTIVO".equalsIgnoreCase(repartidor.getEstado())) {
                log.warn("El repartidor {} no está ACTIVO en courier-service", repartidorId);
                throw new ExcepcionesCourier.RepartidorNoActivoException("El repartidor no se encuentra activo.");
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Repartidor con ID {} no encontrado en courier-service (404)", repartidorId);
            throw new RecursoNoEncontradoException("El repartidor con ID " + repartidorId + " no existe.");
        } catch (Exception e) {
            log.error("Error de comunicación HTTP con courier-service al validar repartidor {}", repartidorId, e);
            throw new ExcepcionesCourier.ErrorComunicacionCourierException("Error al validar el repartidor externo.");
        }
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
        log.info("Cancelando envío correspondiente al Cod. Seguimiento: {}", codigoSeguimiento);
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
        return EnvioResponseDto.builder()
                .id(envio.getId())
                .codigoSeguimiento(envio.getCodigoSeguimiento())
                .peso(envio.getPeso())
                .largo(envio.getLargo())
                .ancho(envio.getAncho())
                .estado(envio.getEstado())
                .build();
    }
}
