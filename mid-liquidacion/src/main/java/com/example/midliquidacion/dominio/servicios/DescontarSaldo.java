package com.example.midliquidacion.dominio.servicios;

import java.math.BigDecimal;

public class DescontarSaldo {

    public BigDecimal restarSaldo(BigDecimal saldoActual, BigDecimal precioFinal){
        return saldoActual.subtract(precioFinal);
    }
}

