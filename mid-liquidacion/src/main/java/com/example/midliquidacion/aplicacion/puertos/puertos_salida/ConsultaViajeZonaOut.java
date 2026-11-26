package com.example.midliquidacion.aplicacion.puertos.puertos_salida;

import com.example.midliquidacion.dominio.modelos.ViajesResponse;
import com.example.midliquidacion.dominio.modelos.ZonaResponse;

//Interfaz de consulta API Viajes y Zonas

public interface ConsultaViajeZonaOut {

    ViajesResponse consultarViaje(String viajeId);

    ZonaResponse consultarZona(String zonaId);

    boolean esPrimerViaje(String usuarioId);

}