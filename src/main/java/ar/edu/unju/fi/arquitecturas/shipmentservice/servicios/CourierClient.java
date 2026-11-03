
package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios;
import ar.edu.unju.fi.arquitecturas.shipmentservice.config.CourierFeignConfig;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.RepartidorExternoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.RepartidorExternoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@FeignClient(name = "courier-service", path = "/api/v1/repartidores", configuration = CourierFeignConfig.class)
public interface CourierClient {
    @GetMapping("/{id}")
    static RepartidorExternoDto obtenerPorId(@PathVariable("id") UUID id) {
        return null;
    }

    @GetMapping("/{id}/disponibilidad")
        Boolean estaDisponible(@PathVariable("id") UUID id);
    }




