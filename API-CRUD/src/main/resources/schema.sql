-- Esquema del servicio cuentas-crud. Debe coincidir con las entidades JPA (*/models):
-- Hibernate valida el esquema al arrancar (spring.jpa.hibernate.ddl-auto=validate).
--
-- Spring lo ejecuta en cada arranque (spring.sql.init.mode=always), por eso todo usa IF NOT EXISTS:
-- si las tablas ya existen no hace nada y no borra datos.
-- Ojo: IF NOT EXISTS no modifica una tabla que ya existe. Si cambia una columna de una tabla
-- existente, hay que aplicar el ALTER TABLE a mano (o borrar la tabla en desarrollo).
--
-- Se puede ejecutar también a mano: psql -d rodar_cuentas -f schema.sql

-- Usuarios: el id es la cédula, la elige el usuario. Borrado lógico con estado INACTIVO.
CREATE TABLE IF NOT EXISTS usuarios (
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
CREATE TABLE IF NOT EXISTS billeteras (
    billetera_id  UUID        PRIMARY KEY,
    usuario_id    VARCHAR(20) NOT NULL,
    saldo         BIGINT      NOT NULL DEFAULT 0,
    CONSTRAINT uk_billeteras_usuario UNIQUE (usuario_id),
    CONSTRAINT fk_billeteras_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (cedula),
    CONSTRAINT ck_billeteras_saldo CHECK (saldo >= 0)
);

-- Movimientos: relación N a 1 con usuarios. referencia es la clave de idempotencia.
CREATE TABLE IF NOT EXISTS movimientos (
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
CREATE INDEX IF NOT EXISTS ix_movimientos_usuario_fecha ON movimientos (usuario_id, creado_en DESC);

-- Liquidaciones: relación N a 1 con usuarios. Solo dos estados: PENDIENTE (al crearla) y REALIZADA.
CREATE TABLE IF NOT EXISTS liquidaciones (
    liquidacion_id  UUID         PRIMARY KEY,
    usuario_id      VARCHAR(20)  NOT NULL,
    estado          VARCHAR(10)  NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT fk_liquidaciones_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (cedula),
    CONSTRAINT ck_liquidaciones_estado CHECK (estado IN ('PENDIENTE', 'REALIZADA'))
);

CREATE INDEX IF NOT EXISTS ix_liquidaciones_usuario ON liquidaciones (usuario_id);
