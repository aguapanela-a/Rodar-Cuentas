package com.example.midliquidacion.infraestructura.DTOs;

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
        this.detalle = detalle;
        this.mensaje = mensaje;
    }


}
