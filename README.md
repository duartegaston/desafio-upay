# Spring Boot API

Aplicación Spring Boot con JPA, Security y PostgreSQL.

## Requisitos

- Java 17+
- Maven 3.9+
- Docker

## Configuración

- Antes que nada clonar el repositorio

```bash
git clone https://github.com/duartegaston/desafio-upay.git
```

Este repositorio NO versiona `src/main/resources/application.properties`. En su lugar incluye una plantilla por si queremos levantar localmente:

- `src/main/resources/application.properties.example`

Podés configurar la app de dos maneras:

- Opción A (recomendada): variables de entorno (vienen configuradas dentro del docker-compose.yml)
  - `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/springbootdb`
  - `SPRING_DATASOURCE_USERNAME=postgres`
  - `SPRING_DATASOURCE_PASSWORD=postgres`
  - `APP_JWT_SECRET=<clave_base64_de_32_bytes>` (generá con: `openssl rand -base64 32`)
  - `APP_JWT_EXPIRATION_MS=3600000`

- Opción B: copiar la plantilla local
  - `cp src/main/resources/application.properties.example src/main/resources/application.properties`
  - Editá los valores según tu entorno local.

## Ejecutar con Docker (API + Postgres)

1) Build y levantar
```bash
docker compose up --build
```

2) Parar
```bash
docker compose down
```

3) Limpiar datos (elimina el volumen de Postgres)
```bash
docker compose down -v
```

## Endpoints principales

- Collection de Postman: `src/main/docs/postman/desafio-upay.postman_collection.json`
   - Descargarla y probar facilmente.

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

- Auth
  - `POST /api/v1/auth/signup` (Crar un usuario)
  - `POST /api/v1/auth/login` (Hacer login para generar un token)

- Calculos
  - `POST /api/v1/calculator/sum` (requiere Bearer)

- Auditoría
  - `GET /api/v1/audit/history` (requiere Bearer)

## Estructura del proyecto

```
src/main/java/com/example/api/
├── config/           # Seguridad y configuración
├── controller/       # REST controllers
├── dto/              # Data Transfer Objects
├── exception/        # Excepciones personalizadas
├── model/            # Entidades JPA
├── repository/       # Repositorios Spring Data
├── security/         # Filtros y servicios JWT
├── service/          # Lógica de negocio
└── SpringBootApiApplication.java
```

- Utilice una estructura de capas Controller, Service y Repository, considerando que se trata de una API pequeña, y es mas facil entenderlo separando bien las responsabilidades.
- Utilice JPA/Hibernate para definir interfaces y contratos de los repositorios.
- Utilice Spring Data JPA para implementar los repositorios.
- Utilice Lombok para simplificar el código.
- Utilice Spring Security para implementar la seguridad.
- Utilice Docker compose para levantar la API y la base de datos.
- Utilice Dockerfile para crear la imagen de la API.

## Base de datos

- Contendra una tabla para persistir los usuarios y otra tabla para persistir los logs de auditoría.

## Kafka

- La API publica eventos de auditoría a un topic de Kafka (audit-events).
- El consumer persiste los eventos en la base de datos.
- Agregue un log para poder verificar que se consumio el evento. "[Kafka][AuditEventConsumer] Consumed event: ..."

## Metricas

- Para consultar metricas se utilizó Prometheus.
  - Endpoint: http://localhost:8080/actuator/prometheus
  - http_server_requests_seconds_max: maximo tiempo de respuesta
  - http_server_requests_seconds: latencia de las peticiones
  - http_server_requests_seconds_count: cantidad de peticiones

## Consideraciones | Deciciones técnicas

- Respecto al desarrollo del endpoint **POST /api/v1/auth/logout**:
  - El backend no mantiene sessiones ya que los JWT son stateless, por lo que no es necesario implementar logout.
  - Pienso que deberia ocurrir en el cliente, por ejemplo, limpiar el localStorage o sessionStorage.
  - Si quisieramos implementar logout, deberiamos mantener un registro de los tokens emitidos y revocarlos cuando el usuario se desloguea.

- Para la sección de auditoría agregue la posibilidad de filtrar por distintos campos, ademas de agregar paginación y ordenar de creación descendente. Este flujo decidí no persistirlo como auditoría ya que no es relevante para el negocio.

- Posibles mejoras:
   - Si esta arquitectura escalara, utilizaria un sistema de roles para los usuarios. (User, Admin)
   - Agregar logs para detectar errores ya sea por CloudWatch, Kibana o similar.
