-- Esquema inicial del servicio cuentas-crud.
-- Nunca editar esta migración una vez aplicada: los cambios van en V3, V4...

CREATE TABLE usuarios (
    id         UUID         PRIMARY KEY,
    nombre     VARCHAR(120) NOT NULL,
    correo     VARCHAR(254) NOT NULL,
    estado     VARCHAR(10)  NOT NULL DEFAULT 'activo'
               CONSTRAINT ck_usuarios_estado CHECK (estado IN ('activo', 'inactivo')),
    creado_en  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Unicidad del correo sin importar mayúsculas. El nombre lo usa UnidadDeTrabajoJpa
-- para traducir la violación a CorreoDuplicadoException.
CREATE UNIQUE INDEX ux_usuarios_correo ON usuarios (lower(correo));

CREATE TABLE billeteras (
    usuario_id      UUID        PRIMARY KEY
                    CONSTRAINT fk_billeteras_usuario REFERENCES usuarios (id),
    saldo           BIGINT      NOT NULL DEFAULT 0
                    CONSTRAINT ck_billeteras_saldo CHECK (saldo >= 0),
    actualizado_en  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE movimientos (
    id                UUID         PRIMARY KEY,
    usuario_id        UUID         NOT NULL
                      CONSTRAINT fk_movimientos_usuario REFERENCES usuarios (id),
    tipo              VARCHAR(10)  NOT NULL
                      CONSTRAINT ck_movimientos_tipo CHECK (tipo IN ('recarga', 'debito')),
    monto             BIGINT       NOT NULL
                      CONSTRAINT ck_movimientos_monto CHECK (monto > 0),
    referencia        VARCHAR(100) NOT NULL,
    descripcion       VARCHAR(255),
    saldo_resultante  BIGINT       NOT NULL
                      CONSTRAINT ck_movimientos_saldo_resultante CHECK (saldo_resultante >= 0),
    creado_en         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    -- Clave de idempotencia. El nombre lo usa UnidadDeTrabajoJpa para reintentar.
    CONSTRAINT ux_movimientos_referencia UNIQUE (referencia)
);

-- Historial por usuario, del más reciente al más antiguo.
CREATE INDEX ix_movimientos_usuario_fecha ON movimientos (usuario_id, creado_en DESC);
