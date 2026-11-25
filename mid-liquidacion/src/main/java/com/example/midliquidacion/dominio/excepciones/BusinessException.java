package com.example.midliquidacion.dominio.excepciones;

public class BusinessException extends RuntimeException {
    private final String codigo;

    public BusinessException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}