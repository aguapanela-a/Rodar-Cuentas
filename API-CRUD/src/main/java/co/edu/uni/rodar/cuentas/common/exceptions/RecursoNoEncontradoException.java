package co.edu.uni.rodar.cuentas.common.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.UUID;

/** 404. */
public class RecursoNoEncontradoException extends ApiException {

    public RecursoNoEncontradoException(String codigo, String mensaje, Map<String, Object> detalle) {
        super(HttpStatus.NOT_FOUND, codigo, mensaje, detalle);
    }

    public static RecursoNoEncontradoException usuario(String cedula) {
        return new RecursoNoEncontradoException("usuario_no_encontrado",
                "No existe un usuario con ese id", Map.of("id", cedula));
    }

    public static RecursoNoEncontradoException liquidacion(UUID liquidacionId) {
        return new RecursoNoEncontradoException("liquidacion_no_encontrada",
                "No existe una liquidación con ese id", Map.of("id", liquidacionId.toString()));
    }
}
