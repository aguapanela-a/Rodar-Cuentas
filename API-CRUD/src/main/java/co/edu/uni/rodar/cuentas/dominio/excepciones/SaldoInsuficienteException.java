package co.edu.uni.rodar.cuentas.dominio.excepciones;

import java.util.LinkedHashMap;
import java.util.Map;

public class SaldoInsuficienteException extends DominioException {

    public SaldoInsuficienteException(long saldo, long monto) {
        super("saldo_insuficiente", "El saldo no alcanza para el débito", detalle(saldo, monto));
    }

    private static Map<String, Object> detalle(long saldo, long monto) {
        Map<String, Object> detalle = new LinkedHashMap<>();
        detalle.put("saldo", saldo);
        detalle.put("monto", monto);
        return detalle;
    }
}
