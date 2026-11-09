package ar.edu.unju.fi.arquitecturas.shipmentservice.config;

import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; // AGREGADA
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // AGREGADA
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Locale;

@Configuration          // NO TE OLVIDES DE ESTA
@EnableMethodSecurity   // Y DE ESTA
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Actuator y endpoints públicos
                        .requestMatchers("/actuator/**", "/error").permitAll()

                        // 2. Gestión de Clientes (Solo ADMIN)
                        .requestMatchers("/clientes/**").hasRole("ADMIN")

                        // 3. Asignar Repartidor (Solo ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/envios/*/asignar-repartidor/*").hasRole("ADMIN")

                        // 4. Cambiar Estado de Envío (ADMIN y REPARTIDOR)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/envios/*/estado").hasAnyRole("ADMIN", "REPARTIDOR")

                        // 5. Cancelar Envío (ADMIN y CLIENTE)
                        .requestMatchers(HttpMethod.POST, "/api/v1/envios/*/cancelar").hasAnyRole("ADMIN", "CLIENTE")

                        // 6. Crear Envío (ADMIN y CLIENTE)
                        .requestMatchers(HttpMethod.POST, "/api/v1/envios").hasAnyRole("ADMIN", "CLIENTE")

                        // 7. Consultar Envíos (ADMIN, REPARTIDOR y CLIENTE)
                        .requestMatchers(HttpMethod.GET, "/api/v1/envios/**").hasAnyRole("ADMIN", "CLIENTE", "REPARTIDOR")

                        // Cualquier otra petición debe estar autenticada
                        .anyRequest().authenticated())
                .oauth2ResourceServer(o -> o.jwt(j ->
                        j.jwtAuthenticationConverter(converter())))
                .build();
    }

    @Bean
    JwtAuthenticationConverter converter() {
        var authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("");

        var converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt ->
                authorities.convert(jwt).stream()
                        .map(a -> a.getAuthority().toUpperCase(Locale.ROOT))
                        .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                        .map(r -> (GrantedAuthority) new SimpleGrantedAuthority(r))
                        .toList()
        );

        return converter;
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