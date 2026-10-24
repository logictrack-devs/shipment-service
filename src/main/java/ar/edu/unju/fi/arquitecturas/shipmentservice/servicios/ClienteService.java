package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Cliente;

import java.util.List;
import java.util.UUID;

/**
 * Operaciones de negocio sobre los clientes de LogiTrack.
 */
public interface ClienteService {

    /**
     * Registra un nuevo cliente.
     *
     * @throws ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.Excepcionescliente.EmailDuplicadoException si el email ya existe
     */
    ClienteResponseDto crear(ClienteRequestDto dto);

    /**
     * Obtiene un cliente por su identificador.
     *
     * @throws ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.Excepcionescliente.ClienteNoEncontradoException si no existe
     */
    ClienteResponseDto obtenerPorId(UUID id);

    /**
     * Lista todos los clientes registrados.
     */
    List<ClienteResponseDto> listar();

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @throws ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.Excepcionescliente.ClienteNoEncontradoException si no existe
     * @throws ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.Excepcionescliente.EmailDuplicadoException si el nuevo email ya pertenece a otro cliente
     */
    ClienteResponseDto actualizar(UUID id, ClienteRequestDto dto);

    /**
     * Devuelve el cliente con ese email y, si no existe, lo crea.
     * Pensado para ser usado desde el alta de envíos.
     */
    Cliente obtenerOCrear(ClienteRequestDto dto);
}