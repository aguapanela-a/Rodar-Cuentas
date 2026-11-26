package com.example.midliquidacion.aplicacion.casosUso;

import com.example.midliquidacion.aplicacion.puertos.puertos_entrada.LiquidarViajeIn;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.ConsultaViajeZonaOut;
import com.example.midliquidacion.dominio.modelos.LiquidacionResponse;


//Orquestador Principal de Liquidaciones

public class LiquidarViajeImpl implements LiquidarViajeIn {
    
    public LiquidarViajeImpl(ConsultaViajeZonaOut consultaViajeZonaOut) {
        this.consultaViajeZonaOut = consultaViajeZonaOut;
    }


    @Override
    public LiquidacionResponse liquidarViaje(String viajeId) {
        //TO DO: Orquestar todo juas juas
        return null;
    }
}