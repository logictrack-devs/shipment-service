package ar.edu.unju.fi.arquitecturas.shipmentservice;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.request.EnvioRequestDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.dto.response.EnvioResponseDto;
import ar.edu.unju.fi.arquitecturas.shipmentservice.enums.EstadoEnvio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion.RecursoNoEncontradoException;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Cliente;
import ar.edu.unju.fi.arquitecturas.shipmentservice.modelo.Envio;
import ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio.ClienteRepository;
import ar.edu.unju.fi.arquitecturas.shipmentservice.repositorio.EnvioRepository;

import ar.edu.unju.fi.arquitecturas.shipmentservice.servicios.impl.EnvioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/****
 * Pruebas unitarias destinadas a testear las
 * funcionalidades de la gestión de envíos
 *
 * @author Equipo LogicTrack devs
 * @version 1.0
 */

@ExtendWith(MockitoExtension.class)
public class EnvioServiceTests {
    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private EnvioServiceImpl envioService;

    private Cliente cliente;
    private Envio envio;
    private UUID envioId;

    @BeforeEach
    void setUp() {
        envioId = UUID.randomUUID();

        cliente = new Cliente();
        UUID clienteId = UUID.randomUUID();
        cliente.setId(clienteId); // Ajustar si el ID no es Long.
        cliente.setNombre("Cliente de prueba");

        envio = new Envio();
        envio.setId(envioId);
        envio.setCodigoSeguimiento("ENV-001");
        envio.setPeso(2.5);
        envio.setLargo(30.0);
        envio.setAncho(20.0);
        envio.setCliente(cliente);
        envio.setEstado(EstadoEnvio.PENDIENTE);
    }

    @Test
    void registrarEnvio_CuandoClienteExiste_RegistraYDevuelveDto() {
        when(clienteRepository.findById(cliente.getId()))
                .thenReturn(Optional.of(cliente));

        when(envioRepository.save(any(Envio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EnvioResponseDto resultado = envioService.registrarEnvio(
                2.5, 30.0, 20.0, cliente, null, null
        );

        assertNotNull(resultado);
        assertEquals(2.5, resultado.getPeso());
        assertEquals(30.0, resultado.getLargo());
        assertEquals(20.0, resultado.getAncho());
        assertEquals(EstadoEnvio.PENDIENTE, resultado.getEstado());
        assertEquals(cliente.getId(), resultado.getClienteId());

        verify(clienteRepository).findById(cliente.getId());
        verify(envioRepository).save(any(Envio.class));
    }

    @Test
    void registrarEnvio_CuandoClienteNoExiste_LanzaExcepcion() {
        when(clienteRepository.findById(cliente.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> envioService.registrarEnvio(
                        2.5, 30.0, 20.0, cliente, null, null
                )
        );

        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void getById_CuandoExisteDevuelveDto() {
        when(envioRepository.findById(envioId))
                .thenReturn(Optional.of(envio));

        EnvioResponseDto resultado = envioService.getById(envioId);

        assertNotNull(resultado);
        assertEquals(envioId, resultado.getId());
        assertEquals("ENV-001", resultado.getCodigoSeguimiento());
        assertEquals(EstadoEnvio.PENDIENTE, resultado.getEstado());
    }

    @Test
    void getById_CuandoNoExisteLanzaExcepcion() {
        when(envioRepository.findById(envioId))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> envioService.getById(envioId)
        );
    }

    @Test
    void getByCodigoSeguimiento_CuandoExisteDevuelveDto() {
        when(envioRepository.findByCodigoSeguimiento("ENV-001"))
                .thenReturn(Optional.of(envio));

        EnvioResponseDto resultado =
                envioService.getByCodigoSeguimiento("ENV-001");

        assertNotNull(resultado);
        assertEquals("ENV-001", resultado.getCodigoSeguimiento());
        assertEquals(envioId, resultado.getId());
    }

    @Test
    void getByCodigoSeguimiento_CuandoNoExisteLanzaExcepcion() {
        when(envioRepository.findByCodigoSeguimiento("ENV-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> envioService.getByCodigoSeguimiento("ENV-999")
        );
    }

    @Test
    void updateEnvio_CuandoExisteActualizaElEstado() {
        EnvioRequestDto request = new EnvioRequestDto(
                "ENV-001", envioId, EstadoEnvio.CANCELADO
        );

        when(envioRepository.findByCodigoSeguimiento("ENV-001"))
                .thenReturn(Optional.of(envio));

        when(envioRepository.save(any(Envio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EnvioResponseDto resultado =
                envioService.updateEnvio("ENV-001", request);

        assertNotNull(resultado);
        assertEquals(EstadoEnvio.CANCELADO, resultado.getEstado());

        verify(envioRepository).save(envio);
    }

    @Test
    void updateEnvio_CuandoNoExisteLanzaExcepcion() {
        EnvioRequestDto request = new EnvioRequestDto(
                "ENV-999", envioId, EstadoEnvio.CANCELADO
        );

        when(envioRepository.findByCodigoSeguimiento("ENV-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> envioService.updateEnvio("ENV-999", request)
        );

        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void listAllEnvios_DevuelveTodosLosEnvios() {
        Envio segundoEnvio = new Envio();
        segundoEnvio.setId(UUID.randomUUID());
        segundoEnvio.setCodigoSeguimiento("ENV-002");
        segundoEnvio.setEstado(EstadoEnvio.PENDIENTE);
        segundoEnvio.setCliente(cliente);

        when(envioRepository.findAll())
                .thenReturn(List.of(envio, segundoEnvio));

        List<EnvioResponseDto> resultado =
                envioService.listAllEnvios();

        assertEquals(2, resultado.size());
        assertEquals("ENV-001", resultado.get(0).getCodigoSeguimiento());
        assertEquals("ENV-002", resultado.get(1).getCodigoSeguimiento());
    }

    @Test
    void listAllEnvios_CuandoNoHayEnviosDevuelveListaVacia() {
        when(envioRepository.findAll()).thenReturn(List.of());

        List<EnvioResponseDto> resultado =
                envioService.listAllEnvios();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listByEstado_DevuelveSoloLosEnviosDelEstadoSolicitado() {
        Envio envioCancelado = new Envio();
        envioCancelado.setId(UUID.randomUUID());
        envioCancelado.setCodigoSeguimiento("ENV-002");
        envioCancelado.setEstado(EstadoEnvio.CANCELADO);
        envioCancelado.setCliente(cliente);

        when(envioRepository.findAll())
                .thenReturn(List.of(envio, envioCancelado));

        List<EnvioResponseDto> resultado =
                envioService.listByEstado(EstadoEnvio.PENDIENTE);

        assertEquals(1, resultado.size());
        assertEquals(
                EstadoEnvio.PENDIENTE,
                resultado.get(0).getEstado()
        );
        assertEquals("ENV-001",
                resultado.get(0).getCodigoSeguimiento());
    }

    @Test
    void cancelarEnvio_CuandoEstaPendienteLoCancela() {
        when(envioRepository.findByCodigoSeguimiento("ENV-001"))
                .thenReturn(Optional.of(envio));

        when(envioRepository.save(any(Envio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        envioService.cancelarEnvio("ENV-001");

        assertEquals(EstadoEnvio.CANCELADO, envio.getEstado());
        verify(envioRepository).save(envio);
    }

    @Test
    void cancelarEnvio_CuandoNoExisteLanzaExcepcion() {
        when(envioRepository.findByCodigoSeguimiento("ENV-999"))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> envioService.cancelarEnvio("ENV-999")
        );

        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void cancelarEnvio_CuandoNoEstaPendienteNoLoModifica() {
        envio.setEstado(EstadoEnvio.CANCELADO);

        when(envioRepository.findByCodigoSeguimiento("ENV-001"))
                .thenReturn(Optional.of(envio));

        envioService.cancelarEnvio("ENV-001");

        assertEquals(
                EstadoEnvio.CANCELADO,
                envio.getEstado()
        );

        verify(envioRepository, never()).save(any(Envio.class));
    }
}
