package co.edu.uni.rodar.cuentas.common.exceptions;

import java.util.Map;

/** 409 saldo_insuficiente (C-HU2): el débito dejaría el saldo negativo. */
public class SaldoInsuficienteException extends ConflictoException {

    public SaldoInsuficienteException(long saldo, long monto) {
        super("saldo_insuficiente", "El saldo no alcanza para el débito", Map.of("saldo", saldo, "monto", monto));
    }
}
