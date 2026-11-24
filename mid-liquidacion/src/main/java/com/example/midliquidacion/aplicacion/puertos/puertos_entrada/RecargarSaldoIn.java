package com.example.midliquidacion.aplicacion.puertos.puertos_entrada;

import java.math.BigDecimal;

public interface RecargarSaldoIn {
    String recargarSaldo(Long usuarioId, BigDecimal valor);
}
