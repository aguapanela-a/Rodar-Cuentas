# API-CRUD (cuentas-crud)

Microservicio del grupo **Cuentas** de la plataforma **Rodar** (scooters y bicicletas compartidas).
Administra usuarios, billeteras y movimientos (recargas y débitos idempotentes).

- **Tecnología:** Java 21 + Spring Boot 4 (Maven)
- **Arquitectura:** hexagonal (puertos y adaptadores)
- **Contrato:** `cuentas-crud.openapi.yaml`. Las rutas, los códigos HTTP, los nombres de campos y el formato de error siguen ese contrato.
- **Moneda:** COP, montos enteros (`long`). **Fechas:** `OffsetDateTime` en UTC (ISO 8601).

> **Estado actual:** la persistencia es **en memoria** (perfil `memoria`). PostgreSQL se implementa en otra rama.
> Los registros de liquidación (`/registros-liquidacion`) y el campo `deudaPendiente` del contrato aún no están implementados.

## Estructura del proyecto

```
API-CRUD/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/co/edu/uni/rodar/cuentas/
    │   │   ├── ApiCrudApplication.java
    │   │   ├── dominio/                                  ← Java puro, no depende de nadie
    │   │   │   ├── excepciones/
    │   │   │   │   ├── DominioException.java             (base: codigo + detalle)
    │   │   │   │   ├── DatosInvalidosException.java
    │   │   │   │   ├── UsuarioNoEncontradoException.java
    │   │   │   │   ├── CorreoDuplicadoException.java
    │   │   │   │   ├── UsuarioInactivoException.java
    │   │   │   │   ├── SaldoInsuficienteException.java
    │   │   │   │   └── ReferenciaEnConflictoException.java
    │   │   │   └── modelos/
    │   │   │       ├── Usuario.java
    │   │   │       ├── Billetera.java
    │   │   │       ├── Movimiento.java
    │   │   │       ├── EstadoUsuario.java
    │   │   │       ├── TipoMovimiento.java
    │   │   │       ├── Paginacion.java
    │   │   │       ├── Pagina.java
    │   │   │       ├── FiltroUsuarios.java
    │   │   │       └── FiltroMovimientos.java
    │   │   ├── aplicacion/                               ← Java puro, depende solo del dominio
    │   │   │   ├── puertos/
    │   │   │   │   ├── entrada/
    │   │   │   │   │   ├── GestionarUsuarioUseCase.java
    │   │   │   │   │   ├── ObtenerSaldoUseCase.java
    │   │   │   │   │   ├── RegistrarMovimientoUseCase.java
    │   │   │   │   │   ├── ConsultarMovimientosUseCase.java
    │   │   │   │   │   ├── CrearUsuarioComando.java
    │   │   │   │   │   ├── ActualizarUsuarioComando.java
    │   │   │   │   │   ├── RegistrarMovimientoComando.java
    │   │   │   │   │   └── ResultadoMovimiento.java
    │   │   │   │   └── salida/
    │   │   │   │       ├── UsuarioRepositorio.java
    │   │   │   │       ├── BilleteraRepositorio.java
    │   │   │   │       ├── MovimientoRepositorio.java
    │   │   │   │       └── UnidadDeTrabajo.java
    │   │   │   └── servicios/
    │   │   │       ├── GestionarUsuarioService.java
    │   │   │       ├── ObtenerSaldoService.java
    │   │   │       ├── RegistrarMovimientoService.java
    │   │   │       └── ConsultarMovimientosService.java
    │   │   └── infraestructura/                          ← único lugar con Spring / Jakarta
    │   │       ├── adaptadores/
    │   │       │   ├── entrada/rest/
    │   │       │   │   ├── UsuarioController.java
    │   │       │   │   ├── BilleteraController.java
    │   │       │   │   ├── MovimientoController.java
    │   │       │   │   ├── HealthController.java
    │   │       │   │   ├── ManejadorErrores.java
    │   │       │   │   └── dto/
    │   │       │   │       ├── CrearUsuarioRequest.java
    │   │       │   │       ├── ActualizarUsuarioRequest.java
    │   │       │   │       ├── RegistrarMovimientoRequest.java
    │   │       │   │       ├── UsuarioResponse.java
    │   │       │   │       ├── BilleteraResponse.java
    │   │       │   │       ├── MovimientoResponse.java
    │   │       │   │       ├── PaginaResponse.java
    │   │       │   │       ├── ErrorResponse.java
    │   │       │   │       ├── SaludResponse.java
    │   │       │   │       └── DtoMapper.java
    │   │       │   └── salida/
    │   │       │       ├── memoria/                      (TEMPORAL, perfil "memoria")
    │   │       │       │   ├── UsuarioMemoriaAdapter.java
    │   │       │       │   ├── BilleteraMemoriaAdapter.java
    │   │       │       │   ├── MovimientoMemoriaAdapter.java
    │   │       │       │   ├── UnidadDeTrabajoMemoria.java
    │   │       │       │   └── DatosSemillaMemoria.java
    │   │       │       └── persistencia/                 (stubs para la rama de BD)
    │   │       │           ├── UsuarioPersistenceAdapter.java
    │   │       │           ├── BilleteraPersistenceAdapter.java
    │   │       │           └── MovimientoPersistenceAdapter.java
    │   │       └── configuracion/
    │   │           ├── BeansConfig.java
    │   │           └── FiltroCabeceraGateway.java
    │   └── resources/
    │       ├── application.properties
    │       └── application-memoria.properties
    └── test/java/co/edu/uni/rodar/cuentas/
        ├── ApiCrudApplicationTests.java                  (contexto Spring, perfil memoria)
        ├── ArquitecturaTest.java                         (ArchUnit: reglas de dependencia)
        ├── dominio/
        │   ├── BilleteraTest.java
        │   └── UsuarioTest.java
        └── aplicacion/
            ├── EntornoEnMemoria.java
            ├── GestionarUsuarioServiceTest.java
            └── RegistrarMovimientoServiceTest.java
```

## Descripción de las capas

| Carpeta | Capa | Contenido |
|---|---|---|
| `dominio/modelos` | Dominio | Entidades con sus reglas (`Usuario`, `Billetera`, `Movimiento`), enums y objetos de consulta (`Paginacion`, `Pagina`, filtros) |
| `dominio/excepciones` | Dominio | Errores de negocio con código estable (`saldo_insuficiente`, `correo_duplicado`...) |
| `aplicacion/puertos/entrada` | Aplicación | Casos de uso que ofrece el servicio (`*UseCase`) y sus comandos/resultados (records) |
| `aplicacion/puertos/salida` | Aplicación | Lo que el núcleo necesita del exterior: un repositorio por entidad y `UnidadDeTrabajo` (atomicidad) |
| `aplicacion/servicios` | Aplicación | Implementación de los casos de uso; orquestan dominio y puertos |
| `infraestructura/adaptadores/entrada/rest` | Infraestructura | Controladores REST por recurso, DTOs del contrato y `ManejadorErrores` |
| `infraestructura/adaptadores/salida/memoria` | Infraestructura | Repositorios y unidad de trabajo en memoria + datos semilla (temporal) |
| `infraestructura/adaptadores/salida/persistencia` | Infraestructura | Adaptadores de PostgreSQL (pendientes, no son beans) |
| `infraestructura/configuracion` | Infraestructura | `BeansConfig` (registro de servicios y `Clock`) y el filtro de la cabecera del gateway |

**Reglas de dependencia** (verificadas por `ArquitecturaTest`):

- El **dominio** no depende de nadie.
- La **aplicación** depende solo del dominio.
- La **infraestructura** depende de las dos.
- Dominio y aplicación son Java puro: no importan `org.springframework`, `jakarta.*` ni Lombok.

## Flujo de una petición

Ejemplo: `POST /billeteras/{usuarioId}/movimientos` (débito de un viaje).

```
Gateway ──(X-Gateway-Secret)──▶ FiltroCabeceraGateway          401 si la cabecera falta o no coincide
                                   │
                                   ▼
                             MovimientoController             @Valid sobre RegistrarMovimientoRequest → 400
                                   │  DtoMapper: request → RegistrarMovimientoComando
                                   ▼
                     RegistrarMovimientoUseCase (puerto entrada)
                                   │
                                   ▼
                       RegistrarMovimientoService             dentro de UnidadDeTrabajo:
                                   │                          1. ¿referencia ya usada? → mismo movimiento (200) o 409
                                   │                          2. usuario existe (404) y está activo (409)
                                   │                          3. billetera bloqueada → recargar / debitar (409 si no alcanza)
                                   │                          4. guardar billetera + movimiento
                                   ▼
          UsuarioRepositorio / BilleteraRepositorio / MovimientoRepositorio (puertos salida)
                                   │
                                   ▼
                    *MemoriaAdapter (hoy)  ·  *PersistenceAdapter (rama BD)
```

Si se lanza una excepción en cualquier punto, `ManejadorErrores` la traduce a `{codigo, mensaje, detalle}` con su código HTTP.

## Perfil `memoria`

Es el perfil por defecto (`spring.profiles.default=memoria`):

- Excluye la autoconfiguración de DataSource, Hibernate/JPA y repositorios JPA, así que **arranca sin base de datos**.
- Activa los adaptadores de `salida/memoria`. Los datos se pierden al reiniciar.
- Al arrancar crea tres usuarios de prueba y escribe sus ids en el log:

| Correo | Estado | Saldo | Recarga inicial |
|---|---|---|---|
| `ana@example.com` | activo | 0 | — |
| `bruno@example.com` | activo | 20000 | `seed-2` |
| `carla@example.com` | inactivo | 5000 | `seed-3` |

## Variables de entorno

| Variable | Por defecto | Uso |
|---|---|---|
| `PORT` | `8080` | Puerto HTTP |
| `GATEWAY_SECRET` | *(vacío)* | Valor que debe traer la cabecera `X-Gateway-Secret`. **Si está vacío, el filtro no bloquea nada** y se registra un WARN al arrancar. Solo para desarrollo local; en el clúster siempre debe tener valor. |

## Ejecución

```bash
# Pruebas (dominio, servicios, arquitectura y arranque del contexto)
./mvnw test

# Arrancar en local (perfil memoria, sin BD, filtro desactivado)
./mvnw spring-boot:run

# Arrancar exigiendo la cabecera del gateway
PORT=8080 GATEWAY_SECRET=mi-secreto ./mvnw spring-boot:run
```

Ejemplo:

```bash
curl -H "X-Gateway-Secret: mi-secreto" http://localhost:8080/usuarios?estado=activo
curl http://localhost:8080/health      # /health no requiere la cabecera
```
