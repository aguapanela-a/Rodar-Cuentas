package com.example.midliquidacion.infraestructura.excepciones;

import com.example.midliquidacion.dominio.excepciones.BusinessException;
import com.example.midliquidacion.dominio.excepciones.ExcepcionesLiquidacion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorDeExcepciones {
    // respuesta de error cn el dto
    @ExceptionHandler(ExcepcionesLiquidacion.class)
    public ResponseEntity<ErrorRespuestaDTO> responderError(ExcepcionesLiquidacion ex){
        ErrorRespuestaDTO respuestaDTO = new ErrorRespuestaDTO(
                ex.getCodigo(),
                ex.getMessage(),
                ex.getDetalle()
        );
        // montos invalidos o solicitudes erroneas es 400
        // saldo insifuciente  409
        HttpStatus estado = ex.getCodigo().contains("invalido")
                ? HttpStatus.BAD_REQUEST
                : HttpStatus.CONFLICT;

        return ResponseEntity.status(estado).body(respuestaDTO);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorRespuestaDTO> responderErrorNegocio(BusinessException ex) {
        HttpStatus estado = switch (ex.getCodigo()) {
            case "usuario_no_encontrado" -> HttpStatus.NOT_FOUND;
            case "pago_rechazado" -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
        return ResponseEntity.status(estado).body(new ErrorRespuestaDTO(ex.getCodigo(), ex.getMessage(), null));
    }
}
