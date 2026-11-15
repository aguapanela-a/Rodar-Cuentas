-- Esquema del servicio cuentas-crud. Debe coincidir con las entidades JPA (*/models):
-- Hibernate valida el esquema al arrancar (spring.jpa.hibernate.ddl-auto=validate).
-- Nunca editar esta migración una vez aplicada en un entorno compartido: los cambios van en V3, V4...

-- Usuarios: el id es la cédula, la elige el usuario. Borrado lógico con estado INACTIVO.
CREATE TABLE usuarios (
    cedula            VARCHAR(20)  PRIMARY KEY,
    primer_nombre     VARCHAR(50)  NOT NULL,
    segundo_nombre    VARCHAR(50),
    primer_apellido   VARCHAR(50)  NOT NULL,
    segundo_apellido  VARCHAR(50),
    correo            VARCHAR(254) NOT NULL,
    estado            VARCHAR(10)  NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT uk_usuarios_correo UNIQUE (correo),
    -- La app guarda el correo en minúsculas; así el UNIQUE también cubre "Ana@x.com" vs "ana@x.com".
    CONSTRAINT ck_usuarios_correo_minusculas CHECK (correo = lower(correo)),
    CONSTRAINT ck_usuarios_estado CHECK (estado IN ('ACTIVO', 'INACTIVO'))
);

-- Billeteras: relación 1 a 1 con usuarios (usuario_id único).
CREATE TABLE billeteras (
    billetera_id  UUID        PRIMARY KEY,
    usuario_id    VARCHAR(20) NOT NULL,
    saldo         BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT uk_billeteras_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_billeteras_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (cedula),
    CONSTRAINT ck_billeteras_saldo CHECK (saldo >= 0)
);

-- Movimientos: relación N a 1 con usuarios. referencia es la clave de idempotencia.
CREATE TABLE movimientos (
    movimiento_id  UUID         PRIMARY KEY,
    usuario_id     VARCHAR(20)  NOT NULL,
    tipo           VARCHAR(10)  NOT NULL,
    monto          BIGINT       NOT NULL,
    referencia     VARCHAR(100) NOT NULL,
    creado_en      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uk_movimientos_referencia UNIQUE (referencia),
    CONSTRAINT fk_movimientos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (cedula),
    CONSTRAINT ck_movimientos_tipo CHECK (tipo IN ('RECARGA', 'DEBITO')),
    CONSTRAINT ck_movimientos_monto CHECK (monto > 0)
);

-- Historial por usuario, del más reciente al más antiguo.
CREATE INDEX ix_movimientos_usuario_fecha ON movimientos (usuario_id, creado_en DESC);
