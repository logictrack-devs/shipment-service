package ar.edu.unju.fi.arquitecturas.shipmentservice.config;

import ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.ExcepcionesCourier;
import ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.RecursoNoEncontradoException;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class CourierFeignConfig {
    @Bean
    RequestInterceptor bearerInterceptor() {
        return template -> {
            var auth =
                    SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwt) {
                template.header("Authorization", "Bearer " +
                        jwt.getToken().getTokenValue());
            }
        };
    }
    @Bean
    ErrorDecoder errorDecoder() {
        return (methodKey, response) -> switch (response.status()) {
            case 404 -> new RecursoNoEncontradoException("El repartidor no existe en courier-service");
            case 401, 403 -> new
                    ExcepcionesCourier.ErrorComunicacionCourierException("courier-service rechazó la autorización");
            default -> new
                    ExcepcionesCourier.ServicioNoDisponibleException("courier-service respondió con error (HTTP " + response.status() + ")");
        };
    }
}


