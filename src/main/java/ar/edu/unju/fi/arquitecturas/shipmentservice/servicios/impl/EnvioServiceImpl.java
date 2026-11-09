
package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.impl;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.EnvioCreateRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.CourierClient;
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
import org.springframework.security.core.Authentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación de los servicios de negocio vinculados
 * a la gestión de envíos.
 *
 * Los repartidores se consultan y validan en courier-service
 * mediante Spring Cloud OpenFeign.
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

    // Cliente OpenFeign para comunicarse con Courier Service
    private final CourierClient courierClient;

    @Override
    @Transactional
    public EnvioResponseDto registrarEnvio(
            Double peso,
            Double largo,
            Double ancho,
            Double alto,
            Cliente cliente,
            Direccion origen,
            Direccion destino) {

        log.info("Registrando envío para el cliente con ID: {}", cliente.getId());

        Cliente clienteEnvio = clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + cliente.getId()
                ));

        String codigoSeguimiento = "ENV-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

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

        nuevoEnvio.setCliente(clienteEnvio);

        Envio envioGuardado = envioRepository.save(nuevoEnvio);

        return mapearResponseDto(envioGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDto getById(UUID id) {

        log.info("Buscando envío con ID: {}", id);

        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Envío no encontrado."
                ));
        validarPertenenciaCliente(envio);
        return mapearResponseDto(envio);
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioResponseDto getByCodigoSeguimiento(String codigoSeguimiento) {

        log.info("Buscando envío con Cod. Seguimiento: {}", codigoSeguimiento);

        Envio envio = envioRepository.findByCodigoSeguimiento(codigoSeguimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Envío no encontrado."
                ));
        validarPertenenciaCliente(envio);
        return mapearResponseDto(envio);
    }

    @Override
    @Transactional
    public EnvioResponseDto updateEnvio(
            String codigoSeguimiento,
            EnvioRequestDto request) {

        Envio envio = envioRepository.findByCodigoSeguimiento(codigoSeguimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Envío no encontrado."
                ));

        envio.setEstado(request.getEstado());

        Envio envioNuevo = envioRepository.save(envio);

        return mapearResponseDto(envioNuevo);
    }
    @Override
    @Transactional
    public EnvioResponseDto registrarEnvio(EnvioCreateRequestDto request) {
        log.info("Registrando envío para el cliente con ID: {}", request.getClienteId());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (esCliente(auth)) {
            Cliente cliente = obtenerClienteAutenticado(auth);
            if (cliente == null && !cliente.getId().equals(request.getClienteId())) {
                throw new AccessDeniedException("No puedes registrar envíos a nombre de otro cliente");
            }
        }

        Cliente clienteEnvio = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + request.getClienteId()
                ));

        String codigoSeguimiento = "ENV-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Envio nuevoEnvio = Envio.builder()
                .codigoSeguimiento(codigoSeguimiento)
                .peso(request.getPeso())
                .largo(request.getLargo())
                .ancho(request.getAncho())
                .alto(request.getAlto())
                .estado(EstadoEnvio.PENDIENTE)
                .direccionOrigen(request.getOrigen())
                .direccionDestino(request.getDestino())
                .build();

        nuevoEnvio.setCliente(clienteEnvio);

        Envio envioGuardado = envioRepository.save(nuevoEnvio);

        return mapearResponseDto(envioGuardado);
    }
   /* @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDto> listAllEnvios() {

        return envioRepository.findAll().stream()
                .map(this::mapearResponseDto)
                .collect(Collectors.toList());
    }

   /* @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDto> listByEstado(EstadoEnvio estado) {

        return envioRepository.findAll().stream()
                .map(this::mapearResponseDto)
                .filter(envio -> envio.getEstado() == estado)
                .collect(Collectors.toList());
    }*/

    @Override
    @Transactional
    public EnvioResponseDto cambiarEstado(
            UUID envioId,
            CambioEstadoRequestDto request) {

        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el envío con ID: " + envioId
                ));

        EstadoEnvio actual = envio.getEstado();
        EstadoEnvio destino = request.getNuevoEstado();

        if (!actual.puedeTransicionarA(destino)) {

            log.warn(
                    "Transición rechazada para envío {}: {} -> {}",
                    envioId, actual, destino
            );

            throw new Excepciones_envio.TransicionEstadoInvalidaException(
                    actual, destino
            );
        }

        if (destino == EstadoEnvio.EN_RUTA) {

            UUID repartidorId = envio.getRepartidorId();

            if (repartidorId == null) {
                throw new Excepciones_envio.RepartidorNoAsignadoException(envioId);
            }

            validarRepartidorActivo(repartidorId);
        }

        envio.setEstado(destino);
        envioRepository.save(envio);

        historialRepository.save(
                HistorialEstadoEnvio.builder()
                        .envio(envio)
                        .estadoAnterior(actual)
                        .estadoNuevo(destino)
                        .fechaCambio(LocalDateTime.now())
                        .observacion(request.getObservacion())
                        .build()
        );

        log.info(
                "Envío {} cambió de {} a {}",
                envioId, actual, destino
        );

        return mapearResponseDto(envio);
    }

    /**
     * Asigna un repartidor externo al envío,
     * guardando únicamente su identificador UUID.
     */
    @Override
    @Transactional
    public EnvioResponseDto asignarRepartidor(
            UUID envioId,
            UUID repartidorId) {

        log.info(
                "Asignando el repartidor {} al envío {}",
                repartidorId, envioId
        );

        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el envío con ID: " + envioId
                ));
        if (envio.getEstado() != EstadoEnvio.PENDIENTE) {
            throw new ExcepcionesCourier.AsignacionNoPermitidaException(envioId, envio.getEstado());
        }

        // Validación mediante OpenFeign
        validarRepartidorActivo(repartidorId);

        envio.setRepartidorId(repartidorId);

        Envio envioGuardado = envioRepository.save(envio);

        log.info(
                "Columna 'repartidor_id' actualizada con éxito para el envío {}",
                envioId
        );

        return mapearResponseDto(envioGuardado);
    }

    /**
     * Consulta Courier Service mediante OpenFeign y verifica
     * que el repartidor exista y tenga estado ACTIVO.
     */
    private void validarRepartidorActivo(UUID repartidorId) {

        RepartidorExternoDto repartidor;

        try {

            repartidor = courierClient.obtenerPorId(repartidorId);

        } catch (feign.RetryableException e) {

            throw new ExcepcionesCourier.ServicioNoDisponibleException(
                    "courierservice no disponible."
            );
        }

        if (repartidor == null ||
                !"ACTIVO".equalsIgnoreCase(repartidor.getEstado())) {

            throw new ExcepcionesCourier.RepartidorNoActivoException(
                    repartidorId,
                    repartidor == null
                            ? "DESCONOCIDO"
                            : repartidor.getEstado()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialEstadoResponseDto> obtenerHistorial(UUID envioId) {

        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el envío con ID: " + envioId
                ));
        validarPertenenciaCliente(envio); // ✅ Ahora sí existe 'envio'
        return historialRepository.findByEnvioIdOrderByFechaCambioAsc(envioId)
                .stream()
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

        log.info(
                "Cancelando envío correspondiente al Cod. Seguimiento: {}",
                codigoSeguimiento
        );

        Envio envio = envioRepository.findByCodigoSeguimiento(codigoSeguimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Envío no encontrado."
                ));
        validarPertenenciaCliente(envio);
        if (envio.getEstado() == EstadoEnvio.PENDIENTE) {

            EnvioRequestDto envioCancelar = new EnvioRequestDto(
                    envio.getCodigoSeguimiento(),
                    envio.getId(),
                    EstadoEnvio.CANCELADO
            );

            updateEnvio(codigoSeguimiento, envioCancelar);

            log.info(
                    "El envío {} ha sido cancelado con éxito.",
                    codigoSeguimiento
            );

        } else {

            log.error(
                    "El envío {} no pudo ser cancelado porque se encuentra en estado {}",
                    codigoSeguimiento,
                    envio.getEstado()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDto> listAllEnvios() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Si es CLIENTE, solo devuelve los envíos que le pertenecen
        if (esCliente(auth)) {
            Cliente cliente = obtenerClienteAutenticado(auth);
            if (cliente == null) {
                return List.of(); // Si el cliente no existe en la BD, lista vacía
            }
            return envioRepository.findByClienteId(cliente.getId()).stream()
                    .map(this::mapearResponseDto)
                    .collect(Collectors.toList());
        }

        // ADMIN o REPARTIDOR (o pruebas unitarias): ven todos los envíos
        return envioRepository.findAll().stream()
                .map(this::mapearResponseDto)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public EnvioResponseDto cancelarEnvioPorId(UUID envioId) {
        log.info("Cancelando envío con ID: {}", envioId);

        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el envío con ID: " + envioId
                ));

        // 1. Validar pertenencia primero (lanza 403 Forbidden si no le pertenece)
        validarPertenenciaCliente(envio);

        // 2. Comprobar regla de negocio (lanza 409 Conflict si no está PENDIENTE)
        if (envio.getEstado() != EstadoEnvio.PENDIENTE) {
            throw new Excepciones_envio.EnvioNoCancelableException(
                    "No se puede cancelar el envío porque se encuentra en estado " + envio.getEstado()
            );
        }

        EstadoEnvio estadoAnterior = envio.getEstado();
        envio.setEstado(EstadoEnvio.CANCELADO);
        Envio envioGuardado = envioRepository.save(envio);

        historialRepository.save(
                HistorialEstadoEnvio.builder()
                        .envio(envioGuardado)
                        .estadoAnterior(estadoAnterior)
                        .estadoNuevo(EstadoEnvio.CANCELADO)
                        .fechaCambio(LocalDateTime.now())
                        .observacion("Envío cancelado a solicitud del usuario")
                        .build()
        );

        log.info("El envío {} ha sido cancelado y persistido con éxito.", envio.getCodigoSeguimiento());
        return mapearResponseDto(envioGuardado);
    }
    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponseDto> listByEstado(EstadoEnvio estado) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Si es CLIENTE, filtra por estado solo dentro de sus propios envíos
        if (esCliente(auth)) {
            Cliente cliente = obtenerClienteAutenticado(auth);
            if (cliente == null) {
                return List.of();
            }
            return envioRepository.findByClienteIdAndEstado(cliente.getId(), estado).stream()
                    .map(this::mapearResponseDto)
                    .collect(Collectors.toList());
        }

        // ADMIN o REPARTIDOR: ven todos los envíos de ese estado
        return envioRepository.findAll().stream()
                .filter(envio -> envio.getEstado() == estado)
                .map(this::mapearResponseDto)
                .collect(Collectors.toList());
    }

    // Métodos auxiliares privados para evaluar el rol y obtener el cliente:
    private boolean esCliente(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE") || a.getAuthority().equals("CLIENTE"));
    }

    private Cliente obtenerClienteAutenticado(Authentication auth) {
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            return null;
        }

        String email = jwtAuth.getToken().getClaimAsString("email");

        if (email == null || email.isBlank()) {
            return null;
        }

        return clienteRepository.findByEmail(email).orElse(null);
    }
    /**
     * Valida que si el usuario autenticado tiene rol CLIENTE, el envío le pertenezca.
     * Si no le pertenece, lanza AccessDeniedException (403 Forbidden).
     */
    private void validarPertenenciaCliente(Envio envio) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (esCliente(auth)) {
            Cliente cliente = obtenerClienteAutenticado(auth);
            if (cliente == null || envio.getCliente() == null || !envio.getCliente().getId().equals(cliente.getId())) {
                throw new AccessDeniedException("No estás autorizado a consultar o modificar un envío de otro cliente");
            }
        }
    }
    private EnvioResponseDto mapearResponseDto(Envio envio) {
        String emailCliente = null;
        UUID clienteId = null;
        String nombreCliente = null;

        if (envio.getCliente() != null) {
            clienteId = envio.getCliente().getId();
            nombreCliente = envio.getCliente().getNombre();
            emailCliente = envio.getCliente().getEmail(); // 👈 NUEVO
        }

        return EnvioResponseDto.builder()
                .id(envio.getId())
                .codigoSeguimiento(envio.getCodigoSeguimiento())
                .peso(envio.getPeso())
                .largo(envio.getLargo())
                .ancho(envio.getAncho())
                .alto(envio.getAlto())
                .estado(envio.getEstado())
                .origen(envio.getDireccionOrigen())
                .destino(envio.getDireccionDestino())
                .clienteId(clienteId)
                .nombreCliente(nombreCliente)
                .emailCliente(emailCliente)                   // 👈 NUEVO
                .repartidorId(envio.getRepartidorId())
                .build();
    }
}
