package com.example.midliquidacion.dominio.servicios;

import com.example.midliquidacion.infraestructura.excepciones.ExcepcionesLiquidacion;

import java.util.HashMap;
import java.util.Map;

public class VerificarRecarga {
    // clase pura en java que recibe por constructor un valor de recarga
    // verifica que ese valor sea positivo
    public VerificarRecarga(){}
    public Boolean verificar(int valor) {
        if(valor < 1){

            Map<String, Object> detalle = new HashMap<>();
            detalle.put("campo", "monto");
            detalle.put("ValorRecibido", Integer.valueOf(valor));

            throw new ExcepcionesLiquidacion(
                    "400: monto invalido",
                    "el monto debe ser positivo",
                    detalle
            );
        }
        return true;
    }
}
