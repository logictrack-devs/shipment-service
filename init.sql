CREATE DATABASE IF NOT EXISTS logitrack;
USE logitrack;

CREATE TABLE IF NOT EXISTS clientes (
                                        id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefono VARCHAR(50)
    );

INSERT INTO clientes (id, nombre, email, telefono)
VALUES ('123e4567-e89b-12d3-a456-426614174000', 'Cliente Inicial', 'cliente@logitrack.com', '3881234567')
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);