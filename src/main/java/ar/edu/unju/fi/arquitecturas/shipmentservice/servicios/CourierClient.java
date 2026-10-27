
package ar.edu.unju.fi.arquitecturas.shipmentservice.servicios;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.RepartidorExternoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CourierClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public RepartidorExternoDto obtenerRepartidorPorId(UUID repartidorId) {
        // Esta URL debe apuntar al endpoint real de tu compañero
        String url = "http://localhost:8082/api/v1/repartidores/" + repartidorId;
        log.info("Consultando repartidor via RestTemplate en: {}", url);
        return restTemplate.getForObject(url, RepartidorExternoDto.class);
    }
}
