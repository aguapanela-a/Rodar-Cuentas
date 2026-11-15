package com.example.midliquidacion.aplicacion.puertos.puertos_entrada;

import com.example.midliquidacion.dominio.modelos.Movimiento;
import java.math.BigDecimal;

public interface RecargarSaldoIn {
    Movimiento recargarSaldo(Long usuarioId, BigDecimal valor);
}
