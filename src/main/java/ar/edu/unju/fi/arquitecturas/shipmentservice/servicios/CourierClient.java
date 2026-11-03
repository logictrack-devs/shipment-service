
package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios;

import ar.edu.unju.fi.arquitecturas.shipmentservice.config.CourierFeignConfig;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.RepartidorExternoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
        name = "courier-service",
        path = "/api/v1/repartidores",
        configuration = CourierFeignConfig.class
)
public interface CourierClient {

    @GetMapping("/{id}")
    RepartidorExternoDto obtenerPorId(@PathVariable("id") UUID id);

    @GetMapping("/{id}/disponibilidad")
    Boolean estaDisponible(@PathVariable("id") UUID id);
}
