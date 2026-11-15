# API-CRUD (cuentas-crud)

Microservicio del grupo **Cuentas** de la plataforma **Rodar** (scooters y bicicletas compartidas).
Administra usuarios, billeteras y movimientos (recargas y débitos idempotentes).

- **Tecnología:** Java 21 + Spring Boot 4 (Maven), PostgreSQL 16, Flyway
- **Contrato:** `cuentas-crud.openapi.yaml`. Las rutas, los códigos HTTP, los nombres de campos y el formato de error siguen ese contrato.
- **Moneda:** COP, montos enteros (`long`). **Fechas:** `OffsetDateTime` en UTC (ISO 8601).

> El código de la aplicación (controladores, servicios, entidades) está pendiente de reimplementar. Por ahora solo existen la clase principal, la configuración y las migraciones de la base de datos.

## Inicio rápido

```bash
# 1. Levantar PostgreSQL 16 (desde la raíz del repositorio)
docker compose up -d

# 2. Arrancar el servicio contra PostgreSQL (desde API-CRUD/)
./mvnw spring-boot:run
```

Al arrancar, Flyway crea las tablas (V1) y carga los 10 usuarios semilla (V2).

### Pruebas

```bash
./mvnw test     # requiere Docker en ejecución (Testcontainers levanta un postgres:16 temporal)
```

## Base de datos

| Comando (desde la raíz) | Efecto |
|---|---|
| `docker compose up -d` | Crea y arranca el contenedor `cuentas-postgres` (base, usuario y clave `cuentas`) |
| `docker compose down` | Lo detiene; **los datos se conservan** en el volumen `cuentas-datos` |
| `docker compose down -v` | Lo detiene y **borra los datos**. Al volver a arrancar, Flyway recrea todo desde cero |

**Si el puerto 5432 ya está ocupado** (otro PostgreSQL local u otro contenedor), use otro puerto del host y apunte la URL a él:

```bash
POSTGRES_PORT=5434 docker compose up -d
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5434/cuentas ./mvnw spring-boot:run
```

**Migraciones** (`src/main/resources/db/migration`):

- `V1__crear_tablas.sql`: tablas `usuarios`, `billeteras` y `movimientos`, con sus restricciones e índices.
- `V2__datos_semilla.sql`: los 10 usuarios de la tabla de abajo.
- **Nunca edite una migración ya aplicada** (Flyway detecta el cambio por checksum y la app no arranca). Los cambios futuros van en `V3__...sql`, `V4__...sql`, etc.

## Variables de entorno

Ver `.env.example` en la raíz. Spring Boot no lee el archivo `.env` por sí solo: exporte las variables en la terminal o configúrelas en IntelliJ (*Run Configuration → Environment variables*).

| Variable | Por defecto | Uso |
|---|---|---|
| `PORT` | `8080` | Puerto HTTP |
| `GATEWAY_SECRET` | *(vacío)* | Valor que debe traer la cabecera `X-Gateway-Secret` (el filtro que lo valida está pendiente de reimplementar). |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/cuentas` | URL JDBC de PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | `cuentas` | Usuario de la BD |
| `SPRING_DATASOURCE_PASSWORD` | `cuentas` | Clave de la BD |
| `DB_POOL_MAX` | `5` | Conexiones máximas por instancia (Hikari) |
| `POSTGRES_PORT` | `5432` | Solo para `docker compose`: puerto del host |

### Azure Database for PostgreSQL

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://<servidor>.postgres.database.azure.com:5432/cuentas?sslmode=require
SPRING_DATASOURCE_USERNAME=<usuario>
SPRING_DATASOURCE_PASSWORD=<clave>
DB_POOL_MAX=5
GATEWAY_SECRET=<el mismo secreto configurado en el gateway>
```

- `?sslmode=require` es obligatorio: Azure rechaza conexiones sin TLS.
- El servidor es compartido entre grupos, así que mantenga `DB_POOL_MAX` bajo (5 por defecto). Con *N* instancias del servicio, el total de conexiones es *N × DB_POOL_MAX*.
- Flyway aplica las migraciones pendientes al arrancar. La base `cuentas` debe existir y el usuario necesita permiso para crear tablas.

## Usuarios semilla (para el grupo Viajes y zonas)

UUID fijos, iguales en todos los entornos que apliquen `V2`. El saldo mínimo para iniciar un viaje es 5000 COP.

| UUID | Nombre | Saldo (COP) | Estado | ¿Puede iniciar viaje? |
|---|---|---:|---|---|
| `11111111-0000-4000-8000-000000000001` | Ana Torres | 0 | activo | No (sin saldo, sin movimientos) |
| `11111111-0000-4000-8000-000000000002` | Bruno Méndez | 50000 | activo | Sí |
| `11111111-0000-4000-8000-000000000003` | Camila Rojas | 20000 | activo | Sí |
| `11111111-0000-4000-8000-000000000004` | Diego Herrera | 5000 | activo | Sí (justo el mínimo) |
| `11111111-0000-4000-8000-000000000005` | Elena Castro | 12500 | activo | Sí |
| `11111111-0000-4000-8000-000000000006` | Felipe Vargas | 3000 | activo | No (saldo insuficiente) |
| `11111111-0000-4000-8000-000000000007` | Gabriela Ortiz | 1500 | activo | No (saldo insuficiente) |
| `11111111-0000-4000-8000-000000000008` | Hugo Salazar | 35000 | activo | Sí |
| `11111111-0000-4000-8000-000000000009` | Isabel Moreno | 8000 | **inactivo** | No (usuario inactivo) |
| `11111111-0000-4000-8000-000000000010` | Julián Pérez | 27000 | activo | Sí |

Cada saldo mayor que 0 tiene una recarga con referencia `seed-NN` (por ejemplo `seed-02`) y id `22222222-0000-4000-8000-0000000000NN`.

## Estructura del proyecto

```
API-CRUD/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/co/edu/uni/rodar/cuentas/
    │   │   ├── ApiCrudApplication.java
    │   │   ├── common/         GlobalExceptionHandler, RecursoNoEncontradoException
    │   │   ├── usuarios/       controllers/ services/ repositories/ models/ dto/
    │   │   ├── billeteras/     controllers/ services/ repositories/ models/ dto/
    │   │   └── movimientos/    controllers/ services/ repositories/ models/ dto/
    │   └── resources/
    │       ├── application.properties
    │       └── db/migration/
    │           ├── V1__crear_tablas.sql
    │           └── V2__datos_semilla.sql
    └── test/java/co/edu/uni/rodar/cuentas/
        └── ApiCrudApplicationTests.java    contexto Spring contra PostgreSQL (Testcontainers)
```
