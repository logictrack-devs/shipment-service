package ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio;

import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Envio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnvioRepository extends JpaRepository<Envio, UUID> {
    Optional<Envio> findByCodigoSeguimiento(String codigoSeguimiento);
    Boolean existsByCodigoSeguimiento(String codigoSeguimiento);
    List<Envio> findByClienteId(UUID clienteId);
    List<Envio> findByClienteIdAndEstado(UUID clienteId, EstadoEnvio estado);
}
