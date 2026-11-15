package com.example.midliquidacion.infraestructura.adaptadores_salida;

import com.example.midliquidacion.aplicacion.puertos.puertos_salida.CrearMovimientoOut;
import com.example.midliquidacion.dominio.modelos.Movimiento;

public class CrearMovimientoOutImpl implements CrearMovimientoOut {
    @Override
    public void guardar(Movimiento movimiento) {
        // TODO: Implementar adaptador de salida en infraestructura
        throw new UnsupportedOperationException("Adaptador de movimientos pendiente de implementación");

    }
    // estos implementan los puertos de salida
    // son las que se comunican con el exterior, en este caso, el CRUD y la pasarela de pagos
}
