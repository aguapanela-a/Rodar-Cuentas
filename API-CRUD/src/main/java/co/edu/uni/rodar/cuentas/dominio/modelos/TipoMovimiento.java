package co.edu.uni.rodar.cuentas.dominio.modelos;

import co.edu.uni.rodar.cuentas.dominio.excepciones.DatosInvalidosException;

public enum TipoMovimiento {
    RECARGA("recarga"),
    DEBITO("debito");

    private final String valor;

    TipoMovimiento(String valor) {
        this.valor = valor;
    }

    /** Representación usada en el contrato (minúscula). */
    public String valor() {
        return valor;
    }

    public static TipoMovimiento desde(String valor) {
        for (TipoMovimiento tipo : values()) {
            if (tipo.valor.equals(valor)) {
                return tipo;
            }
        }
        throw new DatosInvalidosException("tipo", "El tipo debe ser 'recarga' o 'debito'");
    }
}
