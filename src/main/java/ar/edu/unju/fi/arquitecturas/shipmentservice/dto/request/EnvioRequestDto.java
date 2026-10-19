package ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request;

import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioRequestDto {
    @NotBlank(message = "Debe ingresar el código de seguimiento")
    private String codigoSeguimiento;

    @NotBlank(message = "Debe ingresar un id valido.")
    private UUID id;

    @NotBlank(message = "Debe ingresar el nuevo estado")
    private EstadoEnvio estado;
}
