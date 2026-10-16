package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.impl;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.RepartidorRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.RepartidorResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Repartidor;
import ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio.RepartidorRepository;
import ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.RepartidorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación de los servicios de negocio para la gestión de repartidores.
 *
 * <p>
 * Procesa la lógica operativa de registro, filtrado de disponibilidad y consulta
 * paginada de repartidores.
 * </p>
 *
 * @author Equipo LogicTrack Devs
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RepartidorServiceImpl implements RepartidorService {

    private final RepartidorRepository repartidorRepository;

    @Override
    @Transactional
    public RepartidorResponseDto registrarRepartidor(RepartidorRequestDto request) {
        Repartidor repartidor = Repartidor.builder()
                .nombre(request.getNombre())
                .tipoVehiculo(request.getTipoVehiculo())
                .estado(EstadoRepartidor.ACTIVO)
                .build();

        Repartidor guardado = repartidorRepository.save(repartidor);
        return mapearADto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepartidorResponseDto> listarTodosPaginado(Pageable pageable) {
        return repartidorRepository.findAll(pageable)
                .map(this::mapearADto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepartidorResponseDto> listarDisponibles() {
        return repartidorRepository.findByEstado(EstadoRepartidor.ACTIVO).stream()
                .map(this::mapearADto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RepartidorResponseDto obtenerPorId(UUID id) {
        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado con el ID: " + id));
        return mapearADto(repartidor);
    }

    @Override
    @Transactional
    public RepartidorResponseDto asignarEnvio(UUID repartidorId, UUID envioId) {
        // PENDIENTE PARTE 2: Se completará al integrar la entidad Envio de Flavio
        throw new UnsupportedOperationException("El método de asignación estará disponible al integrar la entidad Envio.");
    }

    /**
     * Mapea una entidad {@link Repartidor} a su correspondiente DTO de respuesta.
     *
     * @param repartidor Entidad de dominio a mapear.
     * @return DTO {@link RepartidorResponseDto} con los atributos mapeados.
     */
    private RepartidorResponseDto mapearADto(Repartidor repartidor) {
        return RepartidorResponseDto.builder()
                .id(repartidor.getId())
                .nombre(repartidor.getNombre())
                .tipoVehiculo(repartidor.getTipoVehiculo())
                .estado(repartidor.getEstado())
                .build();
    }
}