package ar.edu.unju.fi.arquitecturas.shipmentservice.config;
import org.springframework.beans.factory.annotation.Value; //  AGREGÁ ESTA

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
    @EnableMethodSecurity
    public class SecurityConfig {

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            return http
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/admin/**").hasRole("ADMIN")
                            .requestMatchers("/api/envios/**").hasAnyRole("CLIENTE","REPARTIDOR","ADMIN")
                            .requestMatchers("/api/repartidores/**").hasAnyRole("REPARTIDOR","ADMIN")
                            .anyRequest().authenticated())
                    .oauth2ResourceServer(o -> o.jwt(j ->
                            j.jwtAuthenticationConverter(converter())))
                    .build();
        }

        @Bean
        JwtAuthenticationConverter converter() {
            var authorities = new JwtGrantedAuthoritiesConverter();
            authorities.setAuthoritiesClaimName("roles");   // mismo claim que emite security
            authorities.setAuthorityPrefix("");              // ya viene "ROLE_"
            var c = new JwtAuthenticationConverter();
            c.setJwtGrantedAuthoritiesConverter(authorities);
            return c;
        }
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    @Value("classpath:certs/public.pem")
    private org.springframework.core.io.Resource publicKeyResource;

    @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        String key = new String(publicKeyResource.getInputStream().readAllBytes())
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decode = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decode);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

        return NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
    }
    }

