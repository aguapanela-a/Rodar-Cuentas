package co.edu.uni.rodar.cuentas.dominio.excepciones;

import java.util.Map;

public class CorreoDuplicadoException extends DominioException {

    public CorreoDuplicadoException(String correo) {
        super("correo_duplicado", "Ya existe un usuario con ese correo", Map.of("correo", correo));
    }
}
