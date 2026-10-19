package ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio;

import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.HistorialEstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
public interface HistorialEstadoEnvioRepository extends JpaRepository<HistorialEstadoEnvio, UUID> {
    List<HistorialEstadoEnvio> findByEnvioIdOrderByFechaCambioAsc(UUID envioId);
}
