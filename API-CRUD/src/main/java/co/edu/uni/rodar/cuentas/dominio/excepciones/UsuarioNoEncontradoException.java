package co.edu.uni.rodar.cuentas.dominio.excepciones;

import java.util.Map;
import java.util.UUID;

public class UsuarioNoEncontradoException extends DominioException {

    public UsuarioNoEncontradoException(UUID id) {
        super("usuario_no_encontrado", "No existe un usuario con ese id", Map.of("id", id.toString()));
    }
}
