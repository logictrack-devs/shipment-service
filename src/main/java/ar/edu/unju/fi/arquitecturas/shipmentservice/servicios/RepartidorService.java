package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.RepartidorRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.RepartidorResponseDto;
import java.util.List;
import java.util.UUID;

/**
 * Interfaz que define el contrato de servicios de negocio para repartidores.
 *
 * <p>
 * Contempla el registro, listado general, listado por disponibilidad y la asignacion a un envio.
 * </p>
 *
 * @author Equipo LogicTrack Devs
 * @version 1.0
 */
public interface RepartidorService {

    /**
     * Registra un nuevo repartidor en la plataforma.
     *
     * @param request Datos del nuevo repartidor.
     * @return Repartidor registrado en formato DTO.
     */
    RepartidorResponseDto registrarRepartidor(RepartidorRequestDto request);

    /**
     * Retorna la lista con todos los repartidores guardados en el sistema.
     *
     * @return Lista de DTOs de repartidores.
     */
    List<RepartidorResponseDto> listarTodos();

    /**
     * Retorna unicamente la lista de repartidores con estado ACTIVO.
     *
     * @return Lista de repartidores disponibles.
     */
    List<RepartidorResponseDto> listarDisponibles();

    /**
     * Busca y obtiene un repartidor especifico a partir de su UUID.
     *
     * @param id Identificador unico del repartidor.
     * @return DTO con la informacion del repartidor.
     */
    RepartidorResponseDto obtenerPorId(UUID id);

    /**
     * Asigna un repartidor activo a un envio determinado.
     *
     * @param repartidorId ID del repartidor a asignar.
     * @param envioId ID del envio al cual se le asignara el repartidor.
     * @return DTO del repartidor asignado.
     */
    RepartidorResponseDto asignarEnvio(UUID repartidorId, UUID envioId);
}