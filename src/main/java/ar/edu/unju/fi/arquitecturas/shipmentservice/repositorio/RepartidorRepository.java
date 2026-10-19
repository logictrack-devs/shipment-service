package ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio;


import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoRepartidor;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Repartidor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la entidad {@link Repartidor}.
 *
 * <p>
 * Proporciona metodos de acceso a datos para realizar consultas y persistencia.
 * </p>
 *
 * @author Equipo LogicTrack Devs
 * @version 1.0
 */
public interface RepartidorRepository extends JpaRepository<Repartidor, UUID> {

    /**
     * Recupera todos los repartidores filtrados por su estado operativo.
     *
     * @param estado Estado por el cual se realizara el filtro (ACTIVO / INACTIVO).
     * @return Lista de repartidores que coinciden con el estado especificado.
     */
    List<Repartidor> findByEstado(EstadoRepartidor estado);
}
