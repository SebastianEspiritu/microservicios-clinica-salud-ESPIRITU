# Laboratorios 11 y 12 – Microservicios con Spring Boot

**Curso:** Desarrollo de Aplicaciones Web Avanzado – Tecsup
**Alumno:** Sebastián Espíritu

- **Lab 11 – Creación de Microservicios:** tienda (cliente, categoría, producto, pedido) y la actividad Clínica "Salud Integral".
- **Lab 12 – Comunicación entre Servicios:** Eureka Server y OpenFeign sobre los servicios de la tienda.

## Proyectos

| Proyecto | Puerto | Base de datos | Tecnología | Endpoint base |
|---|---|---|---|---|
| **eureka-server** | 8761 | – | Boot 3.5 + Eureka Server | dashboard `http://localhost:8761` |
| cliente-service | 8081 | bd_cliente_dev | Boot 3.5 + Eureka Client | `/api/clientes` |
| categoria-service | 8082 | bd_categoria_dev | Boot 3.5 + Eureka Client | `/api/categorias` |
| producto-service | 8083 | bd_producto_dev | Boot 3.5 + Eureka + **OpenFeign** | `/api/productos` |
| pedido-service | 8084 | bd_pedido_dev | Boot 3.5 + Eureka + **OpenFeign** | `/api/pedidos` |
| paciente-service | 8085 | bd_paciente | Boot 4.1 | `/api/pacientes` |
| medico-service | 8086 | bd_medico | Boot 4.1 | `/api/especialidades`, `/api/medicos` |
| cita-service | 8087 | bd_cita | Boot 4.1 + RestClient | `/api/citas` |

Los servicios de la tienda usan **Spring Boot 3.5.15 + Spring Cloud 2025.0.0**, como pide la guía del Lab 12, porque Spring Cloud 2025.0 no es compatible con Spring Boot 4. Los servicios de la clínica siguen en Spring Boot 4.1.0 (Lab 11). Todos se compilan con Java 25.

Cada servicio de la tienda tiene los perfiles `dev` (activo) y `prod` en `application-dev.yml` y `application-prod.yml`.

## Cómo ejecutar

1. Iniciar MySQL (XAMPP), con usuario `root` sin contraseña. Las bases de datos se crean solas.
2. Abrir la carpeta en IntelliJ y recargar Maven (pestaña **m** → 🔄).
3. Ejecutar en este orden:
   1. `EurekaServerApplication`. Esperar a que abra `http://localhost:8761`.
   2. `CategoriaServiceApplication`, `ClienteServiceApplication`, `ProductoServiceApplication` y `PedidoServiceApplication`.
   3. (Clínica, opcional) `PacienteServiceApplication`, `MedicoServiceApplication` y `CitaServiceApplication`.
4. En `http://localhost:8761` deben aparecer **CATEGORIA-SERVICE, CLIENTE-SERVICE, PRODUCTO-SERVICE y PEDIDO-SERVICE**. Al registrarse pueden tardar unos 30 segundos.
5. Importar en Postman `postman/Lab11-Microservicios.postman_collection.json` y ejecutar la carpeta **Lab 12** en orden.

## Lab 12 – Comunicación entre servicios

### Eureka
`eureka-server` (`@EnableEurekaServer`) es el registro de servicios. Cada servicio de la tienda tiene `@EnableDiscoveryClient` y en su YAML se registra en `http://localhost:8761/eureka`.

### producto-service → categoria-service (OpenFeign)
- `CategoriaClient` es un `@FeignClient(name = "categoria-service")`: usa el **nombre registrado en Eureka**, no la URL.
- `Producto.categoria` es `@Transient`: no se guarda en la BD, se completa al consultar.
- Al registrar un producto se valida que la categoría exista. Si no existe, responde 404.

### pedido-service → cliente-service y producto-service (OpenFeign)
- El pedido guarda **solo** `clienteId`, `productoId`, `cantidad`, `fecha` y `estado`. No duplica datos ni tiene relaciones JPA con otros servicios.
- Usa los clientes Feign `ClienteClient` y `ProductoClient`.
- `GET /api/pedidos/{id}`:
  1. Busca el pedido en su BD.
  2. Obtiene el cliente con Feign.
  3. Obtiene el producto con Feign.
  4. Devuelve la respuesta completa, incluido el `total` (precio × cantidad).
- Al registrar valida que el cliente exista y esté ACTIVO, que el producto exista y que haya stock suficiente.
- **No se elimina** un pedido. Sus estados son `PENDIENTE → ATENDIDO | ANULADO`:
  - `PUT /api/pedidos/{id}/estado` cambia el estado.
  - `PUT /api/pedidos/{id}/anular` anula el pedido.

### Validaciones
- **Pedido:** `clienteId` y `productoId` obligatorios; `cantidad` mayor a cero.
- **Cliente:** nombre, apellidos, dirección, estado y `nroDni` obligatorios; el DNI debe tener exactamente 8 dígitos.
- **Producto:** nombre obligatorio y precio mayor a cero.

Ejemplo de `GET /api/pedidos/1`:
```json
{
  "id": 1,
  "fecha": "2026-11-13",
  "cantidad": 2,
  "estado": "PENDIENTE",
  "total": 5000.0,
  "cliente": { "id": 1, "nombre": "Carlos", "apellidos": "Ramirez", "nroDni": "45678912", "...": "..." },
  "producto": { "id": 1, "nombre": "Laptop", "precio": 2500.0, "stock": 10 }
}
```

## Lab 11 – Creación de microservicios

- **Tienda:** CRUD de categoría, cliente y producto con DTO, Bean Validation, `GlobalExceptionHandler` y configuración YAML con perfiles.
- **Clínica "Salud Integral":**
  - **paciente-service:** CRUD de pacientes.
  - **medico-service:** médicos, especialidades y horarios.
  - **cita-service:** registro y estados de citas, y atención. Valida paciente, médico y horario con `RestClient`.

## Conclusiones – Lab 12

1. **Eureka** actúa como directorio de servicios. Cada microservicio se registra con su nombre y los demás lo encuentran sin conocer su IP ni su puerto, lo que facilita escalar y mover servicios.
2. **OpenFeign** permite consumir otro microservicio declarando solo una interfaz con anotaciones, con mucho menos código que hacer las peticiones HTTP a mano.
3. Cada servicio guarda **solo los identificadores** de los datos de otros servicios y completa la información en tiempo real con Feign. Así se evita duplicar datos y se respeta la regla de no compartir bases de datos.
4. Los campos `@Transient` y los DTOs de respuesta permiten devolver información combinada de varios servicios sin crear relaciones JPA entre ellos.
5. La comunicación entre servicios necesita manejar fallos (servicio caído, registro inexistente). Responder con códigos claros (404, 503) y validar los datos antes de guardar hace al sistema más robusto. También es importante que las versiones de Spring Boot y Spring Cloud sean compatibles.

## Conclusiones – Lab 11

1. La arquitectura de microservicios divide el sistema en servicios pequeños e independientes, cada uno con su propia base de datos y puerto.
2. Separar las capas y usar DTOs con Bean Validation desacopla la API de las entidades JPA y evita datos inválidos.
3. `@RestControllerAdvice` centraliza el manejo de errores con respuestas uniformes.
4. Sin FOREIGN KEY entre servicios, las relaciones se validan con comunicación REST.
5. Los perfiles YAML (`dev` y `prod`) cambian la configuración según el ambiente sin modificar el código.
