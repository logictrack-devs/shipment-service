package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.impl;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.Excepcionescliente.ClienteNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.Excepcionescliente.EmailDuplicadoException;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private static final Logger log = LoggerFactory.getLogger(ClienteServiceImpl.class);

    private final ClienteRepository clienteRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public ClienteResponseDto crear(ClienteRequestDto dto) {
        if (clienteRepository.existsByEmail(dto.email())) {
            log.warn("Alta de cliente rechazada: el email {} ya existe", dto.email());
            throw new EmailDuplicadoException(dto.email());
        }
        Cliente guardado = clienteRepository.save(copiarDatos(new Cliente(), dto));
        log.info("Cliente registrado con id {}", guardado.getId());
        return aDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDto obtenerPorId(UUID id) {
        return aDto(buscarOLanzar(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDto> listar() {
        return clienteRepository.findAll().stream()
                .map(this::aDto)
                .toList();
    }

    @Override
    public ClienteResponseDto actualizar(UUID id, ClienteRequestDto dto) {
        Cliente cliente = buscarOLanzar(id);

        boolean cambioEmail = !cliente.getEmail().equalsIgnoreCase(dto.email());
        if (cambioEmail && clienteRepository.existsByEmail(dto.email())) {
            log.warn("Actualización del cliente {} rechazada: el email {} ya existe", id, dto.email());
            throw new EmailDuplicadoException(dto.email());
        }

        Cliente actualizado = clienteRepository.save(copiarDatos(cliente, dto));
        log.info("Cliente {} actualizado", id);
        return aDto(actualizado);
    }

    @Override
    public Cliente obtenerOCrear(ClienteRequestDto dto) {
        return clienteRepository.findByEmail(dto.email())
                .orElseGet(() -> {
                    Cliente nuevo = clienteRepository.save(copiarDatos(new Cliente(), dto));
                    log.info("Cliente creado automáticamente con id {}", nuevo.getId());
                    return nuevo;
                });
    }

    private Cliente buscarOLanzar(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cliente {} no encontrado", id);
                    return new ClienteNoEncontradoException(id);
                });
    }

    private Cliente copiarDatos(Cliente cliente, ClienteRequestDto dto) {
        cliente.setNombre(dto.nombre());
        cliente.setEmail(dto.email());
        cliente.setTelefono(dto.telefono());
        return cliente;
    }

    private ClienteResponseDto aDto(Cliente cliente) {
        return new ClienteResponseDto(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getEmail(),
                cliente.getTelefono());
    }
}