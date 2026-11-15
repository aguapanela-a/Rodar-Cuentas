package co.edu.uni.rodar.cuentas.dominio.modelos;

import co.edu.uni.rodar.cuentas.dominio.excepciones.DatosInvalidosException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.SaldoInsuficienteException;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/** Billetera de un usuario. El saldo está en COP (entero, sin decimales) y nunca es negativo. */
public class Billetera {

    private final UUID usuarioId;
    private long saldo;
    private OffsetDateTime actualizadoEn;

    /** Constructor de reconstrucción para los adaptadores. */
    public Billetera(UUID usuarioId, long saldo, OffsetDateTime actualizadoEn) {
        if (saldo < 0) {
            throw new IllegalArgumentException("El saldo no puede ser negativo");
        }
        this.usuarioId = Objects.requireNonNull(usuarioId, "usuarioId");
        this.saldo = saldo;
        this.actualizadoEn = Objects.requireNonNull(actualizadoEn, "actualizadoEn");
    }

    public static Billetera nueva(UUID usuarioId, OffsetDateTime ahora) {
        return new Billetera(usuarioId, 0, ahora);
    }

    public void recargar(long monto, OffsetDateTime ahora) {
        validarMonto(monto);
        this.saldo = Math.addExact(saldo, monto);
        this.actualizadoEn = ahora;
    }

    /** Si el monto supera el saldo lanza SaldoInsuficienteException sin modificar nada. */
    public void debitar(long monto, OffsetDateTime ahora) {
        validarMonto(monto);
        if (monto > saldo) {
            throw new SaldoInsuficienteException(saldo, monto);
        }
        this.saldo -= monto;
        this.actualizadoEn = ahora;
    }

    static void validarMonto(long monto) {
        if (monto <= 0) {
            throw new DatosInvalidosException("monto", "El monto debe ser mayor que cero");
        }
    }

    public UUID usuarioId() {
        return usuarioId;
    }

    public long saldo() {
        return saldo;
    }

    public OffsetDateTime actualizadoEn() {
        return actualizadoEn;
    }
}
