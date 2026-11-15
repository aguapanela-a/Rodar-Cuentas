package co.edu.uni.rodar.cuentas.dominio.excepciones;

import java.util.Map;

public class UsuarioInactivoException extends DominioException {

    public UsuarioInactivoException() {
        super("usuario_inactivo", "El usuario está desactivado", Map.of());
    }
}
