package co.edu.uni.rodar.cuentas.common.exceptions;

import java.util.Map;

/** 409 usuario_inactivo: el usuario está desactivado (borrado lógico). */
public class UsuarioInactivoException extends ConflictoException {

    public UsuarioInactivoException() {
        super("usuario_inactivo", "El usuario está desactivado", Map.of());
    }
}
