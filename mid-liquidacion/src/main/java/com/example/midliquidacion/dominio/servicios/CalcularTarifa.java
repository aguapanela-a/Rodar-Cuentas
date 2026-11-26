package com.example.midliquidacion.dominio.servicios;

import java.math.BigDecimal;

//Clase base que solo va a usar los datos necesarios, para el saldo total (y cada subdato necesario)
public class CalcularTarifa {
    
    public BigDecimal calcularTarifa(Integer minutos) {

        int minutosSeguros = (minutos != null) ? minutos : 0;

        return BigDecimal.valueOf(minutosSeguros)
                         .multiply(BigDecimal.valueOf(350))
                         .add(BigDecimal.valueOf(1000));
    }


    public BigDecimal zonaRecargo(BigDecimal tarifaBase){
        return tarifaBase.multiply(BigDecimal.valueOf(0.2));
    }

    public BigDecimal descuentoPrimerViaje(BigDecimal tarifaBase){
        return tarifaBase.multiply(BigDecimal.valueOf(0.5));
    }

    public BigDecimal calcularPrecioFinal(BigDecimal tarifaBase, BigDecimal zonaRecargo, BigDecimal descuento) {
        BigDecimal recargoReal = (zonaRecargo != null) ? zonaRecargo : BigDecimal.ZERO;
        BigDecimal descuentoReal = (descuento != null) ? descuento : BigDecimal.ZERO;

        return tarifaBase.add(recargoReal).subtract(descuentoReal);
    }
}
