package com.example.midliquidacion.dominio.excepciones;

import lombok.Getter;

@Getter
public class ExcepcionesLiquidacion extends RuntimeException{
    // capturar excepciones BusinessException

    private final String codigo;
    private final Object detalle;

    public ExcepcionesLiquidacion(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
        this.detalle = new Object();
    }

    public ExcepcionesLiquidacion(String codigo, String mensaje, Object detalle){
        super(mensaje);
        this.codigo = codigo;
        this.detalle = detalle;
    }


}
