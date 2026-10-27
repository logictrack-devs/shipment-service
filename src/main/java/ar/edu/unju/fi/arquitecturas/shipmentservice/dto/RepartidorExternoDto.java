package ar.edu.unju.fi.arquitecturas.shipmentservice.dto;


import lombok.Data;
import java.util.UUID;

@Data // Lombok genera los getters y setters automáticamente
public class RepartidorExternoDto {
    private UUID id;
    private String estado; // Acá vas a recibir si está "ACTIVO" o "INACTIVO"
}


