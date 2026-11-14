# Laboratorio 11 – Creación de Microservicios

**Curso:** Desarrollo de Aplicaciones Web Avanzado – Tecsup
**Alumna:** Kiara Paola Alburqueque Arzapalo

Spring Boot 4.1.0 · Java 25 · Maven · MySQL · Spring Data JPA · Bean Validation

## Microservicios

| Microservicio | Puerto | Base de datos | Endpoint base |
|---|---|---|---|
| cliente-service | 8081 | bd_cliente | `/api/clientes` |
| categoria-service | 8082 (dev) / 9092 (prod) | bd_categoria_dev / bd_categoria | `/api/categorias` |
| producto-service | 8083 | bd_producto | `/api/productos` |
| pedido-service | 8084 | bd_pedido | `/api/pedidos` |
| **paciente-service** | 8085 | bd_paciente | `/api/pacientes` |
| **medico-service** | 8086 | bd_medico | `/api/especialidades`, `/api/medicos`, `/api/medicos/{id}/horarios` |
| **cita-service** | 8087 | bd_cita | `/api/citas` |

Cada microservicio tiene la estructura `controller`, `model`, `dto`, `exception`, `repository`, `services` (paquete `com.tecsup`).

## Cómo ejecutarlo

1. Iniciar MySQL (XAMPP) con usuario `root` sin contraseña. Si tienes contraseña, cámbiala en cada `application.yml`.
2. Ejecutar `sql/01_crear_bases_de_datos.sql`. Opcional: las URLs ya incluyen `createDatabaseIfNotExist=true`, y las tablas las crea JPA.
3. En IntelliJ: **File → Open → carpeta `microservicios`**. El `pom.xml` raíz importa los 7 proyectos Maven.
4. Configurar **JDK 25** en *File → Project Structure → SDK*.
5. Ejecutar la clase `*ServiceApplication` de cada servicio.
   - En la clínica, levantar **paciente-service** y **medico-service** antes de **cita-service**.
6. Importar en Postman `postman/Lab11-Microservicios.postman_collection.json` y ejecutar las carpetas en orden.

## Parte guiada

- **categoria-service**: código de la guía y perfiles `application.yml` / `application-dev.yml` / `application-prod.yml`. El perfil activo es `dev`.
- **cliente-service** y **producto-service**: siguen el mismo patrón de Categoría (DTO con validaciones, `GlobalExceptionHandler` y `ResourceNotFoundException`).
- **pedido-service**: código de la guía.
  - Se añadió `@JsonIgnore` en `DetallePedido.pedido` para evitar la recursión infinita al convertir a JSON.
- Todos usan `application.yml`; se eliminó `application.properties`.

## Actividad – Clínica "Salud Integral"

**paciente-service**
- CRUD completo de pacientes.
- Valida el DNI (8 dígitos y único), el sexo (M/F), el correo y la fecha de nacimiento.

**medico-service**
- CRUD de especialidades y médicos (el CMP es único).
- Registro de horarios por día (`LUNES`…`DOMINGO`), con control de horarios que se cruzan.
- Al eliminar un médico se borran sus horarios en cascada.

**cita-service**
- Registrar, listar (con filtros `?idPaciente=`, `?idMedico=` y `?estado=`), buscar por ID, actualizar el estado y cancelar.
- **Comunicación entre microservicios** con `RestClient` (paquete `client`). Al registrar una cita:
  1. Consulta a **paciente-service** que el paciente exista y esté activo.
  2. Consulta a **medico-service** que el médico exista y esté activo.
  3. Verifica que la fecha y hora estén dentro del **horario del médico**.
  4. Verifica que ni el médico ni el paciente tengan otra cita a esa hora.
- Si un servicio está caído, responde **503** con un mensaje claro.
- `GET /api/citas/{id}/detalle` devuelve la cita junto con los datos del paciente y del médico.
- Estados y transiciones permitidas:
  - PROGRAMADA → CONFIRMADA o CANCELADA
  - CONFIRMADA → ATENDIDA o CANCELADA
  - ATENDIDA y CANCELADA son estados finales.
- `PUT /api/citas/{id}/cancelar` cancela la cita.
- `POST /api/citas/{id}/atencion` registra la **atención** (diagnóstico, tratamiento y observaciones) de una cita CONFIRMADA y la pasa a ATENDIDA.

> Las tablas `cita` y `atencion` guardan `id_paciente` e `id_medico` **sin FOREIGN KEY física**. En microservicios cada servicio tiene su propia base de datos, así que la integridad se valida llamando a los otros servicios.

## Conclusiones

1. La arquitectura de microservicios divide el sistema en servicios pequeños e independientes. Cada uno tiene su propia base de datos y su propio puerto, así que se puede desarrollar, desplegar y escalar por separado.
2. Separar las capas (controller, service, repository, model, dto) y usar DTOs con Bean Validation (`@NotBlank`, `@Pattern`, `@Email`) desacopla la API de las entidades JPA y evita que entren datos inválidos.
3. Un `@RestControllerAdvice` centraliza el manejo de errores y devuelve respuestas uniformes (400, 404, 409, 503), lo que facilita el consumo de la API desde Postman o un frontend.
4. Como cada microservicio tiene su base de datos, no se pueden usar FOREIGN KEY entre servicios. Las relaciones se guardan como IDs y se validan con comunicación REST (`RestClient`), como hace cita-service con paciente-service y medico-service.
5. Los archivos YAML con perfiles (`dev` y `prod`) permiten cambiar la base de datos, el puerto y otras propiedades según el ambiente sin modificar el código; basta con cambiar `spring.profiles.active`.
