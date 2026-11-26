package com.example.midliquidacion.aplicacion.puertos.puertos_entrada;

import com.example.midliquidacion.dominio.modelos.LiquidacionResponse;

//Interfaz para liquidar el viaje
public interface LiquidarViajeIn {
    LiquidacionResponse liquidarViaje(String viajeId);
}
