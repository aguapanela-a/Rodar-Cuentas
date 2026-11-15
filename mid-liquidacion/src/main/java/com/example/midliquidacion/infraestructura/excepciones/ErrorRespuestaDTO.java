package com.example.midliquidacion.infraestructura.excepciones;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ErrorRespuestaDTO {
    private String codigo;
    private String mensaje;
    private Object detalle;

    public ErrorRespuestaDTO (String codigo, String mensaje, Object detalle){
        this.codigo = codigo;
        this.detalle = mensaje;
        this.mensaje = mensaje;
    }


}
