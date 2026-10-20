package ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio;

import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
    Optional<Cliente> findById(UUID id);
    boolean existsById(UUID id);

    List<Cliente> id(UUID id);
}
