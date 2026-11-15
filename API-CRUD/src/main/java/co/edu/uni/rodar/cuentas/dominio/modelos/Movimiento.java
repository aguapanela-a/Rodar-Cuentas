package co.edu.uni.rodar.cuentas.dominio.modelos;

import co.edu.uni.rodar.cuentas.dominio.excepciones.DatosInvalidosException;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/** Recarga o débito ya aplicado a una billetera. Es inmutable. */
public class Movimiento {

    private static final int REFERENCIA_MAX = 100;
    private static final int DESCRIPCION_MAX = 255;

    private final UUID id;
    private final UUID usuarioId;
    private final TipoMovimiento tipo;
    private final long monto;
    private final String referencia;
    private final String descripcion;
    private final long saldoResultante;
    private final OffsetDateTime creadoEn;

    /** Constructor de reconstrucción para los adaptadores. */
    public Movimiento(UUID id, UUID usuarioId, TipoMovimiento tipo, long monto, String referencia,
                      String descripcion, long saldoResultante, OffsetDateTime creadoEn) {
        this.id = Objects.requireNonNull(id, "id");
        this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId");
        this.tipo = Objects.requireNonNull(tipo, "tipo");
        this.monto = monto;
        this.referencia = Objects.requireNonNull(referencia, "referencia");
        this.descripcion = descripcion;
        this.saldoResultante = saldoResultante;
        this.creadoEn = Objects.requireNonNull(creadoEn, "creadoEn");
    }

    public static Movimiento registrar(UUID usuarioId, TipoMovimiento tipo, long monto, String referencia,
                                       String descripcion, long saldoResultante, OffsetDateTime ahora) {
        if (tipo == null) {
            throw new DatosInvalidosException("tipo", "El tipo es obligatorio");
        }
        Billetera.validarMonto(monto);
        validarReferencia(referencia);
        validarDescripcion(descripcion);
        return new Movimiento(UUID.randomUUID(), usuarioId, tipo, monto, referencia, descripcion,
                saldoResultante, ahora);
    }

    public static void validarReferencia(String referencia) {
        if (referencia == null || referencia.isBlank()) {
            throw new DatosInvalidosException("referencia", "La referencia es obligatoria");
        }
        if (referencia.length() > REFERENCIA_MAX) {
            throw new DatosInvalidosException("referencia",
                    "La referencia no puede superar " + REFERENCIA_MAX + " caracteres");
        }
    }

    public static void validarDescripcion(String descripcion) {
        if (descripcion != null && descripcion.length() > DESCRIPCION_MAX) {
            throw new DatosInvalidosException("descripcion",
                    "La descripción no puede superar " + DESCRIPCION_MAX + " caracteres");
        }
    }

    /** Idempotencia: ¿un reintento con esta referencia trae los mismos datos que el original? */
    public boolean mismosDatos(UUID usuarioId, TipoMovimiento tipo, long monto) {
        return this.usuarioId.equals(usuarioId) && this.tipo == tipo && this.monto == monto;
    }

    public UUID id() {
        return id;
    }

    public UUID usuarioId() {
        return usuarioId;
    }

    public TipoMovimiento tipo() {
        return tipo;
    }

    public long monto() {
        return monto;
    }

    public String referencia() {
        return referencia;
    }

    public String descripcion() {
        return descripcion;
    }

    public long saldoResultante() {
        return saldoResultante;
    }

    public OffsetDateTime creadoEn() {
        return creadoEn;
    }
}
