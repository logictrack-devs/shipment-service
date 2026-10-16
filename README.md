# **Logitrack** - Logística integral y seguimiento de envíos
Logitrack es una plataforma de logística integral orientada a la gestión y seguimiento de envíos para comercios electrónicos y entregas de última milla
>Desarrollo y Arquitecturas Avanzadas de Software - Universidad Nacional de Jujuy.
><br/>Equipo de desarrollo: 
> - Orrabaliz Valentina
> - Puca Micaela
> - Reinoso Flavio
><br/><center>-2026-</center>
---
## Características
- **Gestión de envíos**: Registrar nuevos envíos, consultar envíos registrados y cancelación de los mismos
- **Actualización de estado**: la plataforma permite actualizar de manera intuitiva y eficiente el estado de los envíos, coherentemente con el ciclo de vida del mismo.
- **Auditoría**: los cambios realizados se registran automáticamente en la base de datos con los datos necesarios para su posterior auditoría.
- **Gestión y asignación de repartidores**: permite el registro de nuevos repartidores en el sistema, asimismo, las tareas de consulta y detalles referentes a ellos. Los envíos se asignan a aquellos repartidores en estado ACTIVO.
---
## Stack tecnológico

- **Lenguaje y framework**: Java 25 + Spring Boot 3.x
- **Persistencia de datos**: Spring Data JPA + MySQL
- **Manejo de datos**: DTO's
- **Empaquetamiento y despliegue**: Docker
- **Documentación del proyecto**: Springdoc OpenAPI/Swagger
---
## Consideraciones previas

1. **Java versión 25 o superior**:
```bash
winget install Oracle.JDK.25
```
2. **MySQL**:
```bash
winget install Oracle.MySQL
```
3. **Docker**:
```bash
winget install Docker.DockerDesktop
```
---
## Instalaciones

Clonar el repositorio:
```git
git clone https://github.com/logictrack-devs/shipment-service.git
```
---
## Estructura del proyecto
```bash
shipment-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ar.edu.unju.fi.arquitecturas.shipmentservice/
│   │   │        ├── config/
│   │   │        ├── controlador/
│   │   │        ├── dto/
│   │   │        ├── enums/
│   │   │        ├── excepcion/
│   │   │        ├── modelo/
│   │   │        ├── repositorio/
│   │   │        ├── servicios/
│   │   │        │    └── impl/
│   │   │        └── ShipmentServiceApplication
│   │   └── resources/           
│   │         └── application.yaml      #Archivo de configuración
│   └── test/
│         └──  java/
│                └── ar.edu.unju.fi.arquitecturas.shipmentservice
│                      └──ShipmentServiceApplicationTests
├── pom.xml       #Dependencias
└──README.md               
```
---
## Arquitectura

---
### Tablas

---
| Tabla | Propósito |
|---|---|
| Cliente | Registro y manejo de clientes. |
| Repartidor | Tabla de gestión de repartidores del servicio. |
| Envío | Gestión y entrega de paquetes. |
| ... | ... |

---

## Ejemplos de uso de endpoints de la API

---

### Rutas autorizadas

| Endpoint | Método | Descripción |
| --- | --- | --- |
| ... | ... | ... |

---

## Variables de entorno

| Variable | Descripción |
| --- |--- |
| ... | ... |

---

**Sistema desarrollado por alumnos de la carrera Ingeniería Informática, solo con fines académicos.**
