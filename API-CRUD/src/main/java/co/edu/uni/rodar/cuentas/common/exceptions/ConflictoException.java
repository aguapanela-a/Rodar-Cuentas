package co.edu.uni.rodar.cuentas.common.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

/** 409: datos repetidos (cédula, correo, referencia) o una regla de negocio que impide la operación. */
public class ConflictoException extends ApiException {

    public ConflictoException(String codigo, String mensaje, Map<String, Object> detalle) {
        super(HttpStatus.CONFLICT, codigo, mensaje, detalle);
    }
}
