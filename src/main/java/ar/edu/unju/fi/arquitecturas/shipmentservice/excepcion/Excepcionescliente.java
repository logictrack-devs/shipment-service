package ar.edu.unju.fi.arquitecturas.shipmentservice.excepcion;

import java.util.UUID;

/**
 * Agrupa las excepciones de negocio relacionadas con los clientes.
 */
public class Excepcionescliente {

    private Excepcionescliente() {
    }

    /**
     * Se lanza cuando no existe un cliente con el identificador solicitado.
     */
    public static class ClienteNoEncontradoException extends RuntimeException {

        public ClienteNoEncontradoException(UUID id) {
            super("No existe un cliente con id " + id);
        }
    }

    /**
     * Se lanza cuando se intenta registrar o modificar un cliente con un email que ya está en uso.
     */
    public static class EmailDuplicadoException extends RuntimeException {

        public EmailDuplicadoException(String email) {
            super("Ya existe un cliente con el email " + email);
        }
    }
}

