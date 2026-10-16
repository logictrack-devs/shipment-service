package ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio;

import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Envio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EnvioRepository extends JpaRepository<Envio, UUID> {
    Optional<Envio> findByCodigoSeguimiento(String codigoSeguimiento);
    Boolean existsByCodigoSeguimiento(String codigoSeguimiento);

}
