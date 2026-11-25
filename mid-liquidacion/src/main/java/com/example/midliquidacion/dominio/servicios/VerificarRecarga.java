package com.example.midliquidacion.dominio.servicios;

import com.example.midliquidacion.dominio.excepciones.BusinessException;

import java.math.BigDecimal;

public class VerificarRecarga {
    public Boolean verificar(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new BusinessException("400: monto invalido", "el monto debe ser positivo");
        }
        if (valor.stripTrailingZeros().scale() > 0) {
            throw new BusinessException("400: monto invalido", "el monto debe ser entero en pesos COP");
        }
        return true;
    }
}
