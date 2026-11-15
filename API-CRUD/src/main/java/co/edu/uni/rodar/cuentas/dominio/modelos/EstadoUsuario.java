package co.edu.uni.rodar.cuentas.dominio.modelos;

import co.edu.uni.rodar.cuentas.dominio.excepciones.DatosInvalidosException;

public enum EstadoUsuario {
    ACTIVO("activo"),
    INACTIVO("inactivo");

    private final String valor;

    EstadoUsuario(String valor) {
        this.valor = valor;
    }

    /** Representación usada en el contrato (minúscula). */
    public String valor() {
        return valor;
    }

    public static EstadoUsuario desde(String valor) {
        for (EstadoUsuario estado : values()) {
            if (estado.valor.equals(valor)) {
                return estado;
            }
        }
        throw new DatosInvalidosException("estado", "El estado debe ser 'activo' o 'inactivo'");
    }
}
