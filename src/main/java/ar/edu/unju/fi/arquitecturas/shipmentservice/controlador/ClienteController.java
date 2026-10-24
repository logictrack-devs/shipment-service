package ar.edu.unju.fi.arquitecturas.shipmentservice.controlador;

import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.ClienteRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.ClienteResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Gestión de los clientes que registran envíos")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @Operation(summary = "Registrar un cliente",
            description = "Devuelve 201 si se crea, 400 si los datos son inválidos y 409 si el email ya existe")
    public ResponseEntity<ClienteResponseDto> crear(@Valid @RequestBody ClienteRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.crear(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos los clientes")
    public List<ClienteResponseDto> listar() {
        return clienteService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un cliente por id",
            description = "Devuelve 404 si no existe")
    public ClienteResponseDto obtenerPorId(@PathVariable UUID id) {
        return clienteService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cliente",
            description = "Devuelve 404 si no existe y 409 si el nuevo email ya pertenece a otro cliente")
    public ClienteResponseDto actualizar(@PathVariable UUID id, @Valid @RequestBody ClienteRequestDto dto) {
        return clienteService.actualizar(id, dto);
    }
}
