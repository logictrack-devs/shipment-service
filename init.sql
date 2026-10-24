
CREATE DATABASE IF NOT EXISTS logitrack;
USE logitrack;

CREATE TABLE IF NOT EXISTS clientes (
    id BINARY(16) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefono VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

INSERT INTO clientes (id, nombre, email, telefono, created_at, updated_at)
VALUES (UUID_TO_BIN('123e4567-e89b-12d3-a456-426614174000') ,'Cliente Inicial', 'cliente@logitrack.com', '3881234567', NOW(), NOW())
    ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);