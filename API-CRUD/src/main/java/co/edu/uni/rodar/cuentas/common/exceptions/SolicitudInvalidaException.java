package co.edu.uni.rodar.cuentas.common.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

/** 400 solicitud_invalida, para validaciones que se hacen en el service y no en el DTO. */
public class SolicitudInvalidaException extends ApiException {

    public SolicitudInvalidaException(String mensaje, String campo) {
        super(HttpStatus.BAD_REQUEST, "solicitud_invalida", mensaje, Map.of("campo", campo));
    }
}
