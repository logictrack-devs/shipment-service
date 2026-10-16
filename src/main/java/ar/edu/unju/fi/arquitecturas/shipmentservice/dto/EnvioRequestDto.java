package ar.edu.unju.fi.arquitecturas.shipmentservice.dto;

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
}
