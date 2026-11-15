# API-CRUD

Microservicio del proyecto **Rodar-Cuentas** que gestiona la persistencia de usuarios, billeteras y movimientos del sistema de scooters y bicicletas compartidas.

- **Tecnología:** Java + Spring Boot (Maven)
- **Dependencias principales:** Spring Web MVC, Spring Data JPA, Spring Security, Lombok
- **Arquitectura:** Hexagonal (puertos y adaptadores)

## Estructura del proyecto

```
API-CRUD/
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── co/edu/uni/rodar/cuentas/
│   │   │       ├── ApiCrudApplication.java
│   │   │       ├── dominio/
│   │   │       │   ├── excepciones/
│   │   │       │   │   └── package-info.java
│   │   │       │   └── modelos/
│   │   │       │       ├── Billetera.java
│   │   │       │       ├── Movimiento.java
│   │   │       │       └── Usuario.java
│   │   │       ├── aplicacion/
│   │   │       │   ├── puertos/
│   │   │       │   │   ├── entrada/
│   │   │       │   │   │   ├── GestionarUsuarioUseCase.java
│   │   │       │   │   │   └── ObtenerSaldoUseCase.java
│   │   │       │   │   └── salida/
│   │   │       │   │       ├── BilleteraRepositorio.java
│   │   │       │   │       ├── MovimientoRepositorio.java
│   │   │       │   │       └── UsuarioRepositorio.java
│   │   │       │   └── servicios/
│   │   │       │       └── ObtenerSaldoService.java
│   │   │       └── infraestructura/
│   │   │           └── adaptadores/
│   │   │               ├── entrada/
│   │   │               │   └── rest/
│   │   │               │       └── BilleteraController.java
│   │   │               └── salida/
│   │   │                   └── persistencia/
│   │   │                       ├── BilleteraPersistenceAdapter.java
│   │   │                       ├── MovimientoPersistenceAdapter.java
│   │   │                       └── UsuarioPersistenceAdapter.java
│   │   └── resources/
│   │       └── application.properties.example
│   └── test/
│       └── java/
│           └── co/edu/uni/rodar/cuentas/
│               └── ApiCrudApplicationTests.java
├── .gitattributes
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Descripción de las capas

| Carpeta | Capa | Contenido |
|---|---|---|
| `dominio/modelos` | Dominio | Entidades del negocio: `Usuario`, `Billetera`, `Movimiento` |
| `dominio/excepciones` | Dominio | Excepciones del negocio (reservado, aún vacío) |
| `aplicacion/puertos/entrada` | Aplicación | Casos de uso que ofrece el servicio: `GestionarUsuarioUseCase`, `ObtenerSaldoUseCase` |
| `aplicacion/puertos/salida` | Aplicación | Un repositorio por entidad: `UsuarioRepositorio`, `BilleteraRepositorio`, `MovimientoRepositorio` |
| `aplicacion/servicios` | Aplicación | Implementación de los casos de uso: `ObtenerSaldoService` |
| `infraestructura/adaptadores/entrada/rest` | Infraestructura | Controladores REST por recurso: `BilleteraController` |
| `infraestructura/adaptadores/salida/persistencia` | Infraestructura | Implementación de los repositorios: `UsuarioPersistenceAdapter`, `BilleteraPersistenceAdapter`, `MovimientoPersistenceAdapter` |

Reglas de dependencia:

- **Dominio** no depende de nadie.
- **Aplicación** depende solo del dominio.
- **Infraestructura** depende de aplicación y dominio. Es la única capa que usa Spring y JPA.
- Dominio y aplicación son Java puro, sin imports de `org.springframework` ni `jakarta.persistence`.

Flujo de una petición:

```
Controller (rest) → UseCase (puerto entrada) → Service → Repositorio (puerto salida) → PersistenceAdapter
```

## Configuración

Copiar `src/main/resources/application.properties.example` a `application.properties` y completar los valores (conexión a base de datos, etc.).

## Ejecución

```bash
./mvnw spring-boot:run
```
