package com.example.midliquidacion.aplicacion.puertos.puertos_salida;

import com.example.midliquidacion.dominio.modelos.Movimiento;

public interface CrearMovimientoOut {
    void guardar(Movimiento movimiento);
}
