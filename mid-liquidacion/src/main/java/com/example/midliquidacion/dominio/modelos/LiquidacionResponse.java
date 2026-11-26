package com.example.midliquidacion.dominio.modelos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LiquidacionResponse {
    private BigDecimal tarifaBase;
    private BigDecimal recargo;
    private BigDecimal descuento;
    private BigDecimal total;
    private BigDecimal saldoRestante;
}