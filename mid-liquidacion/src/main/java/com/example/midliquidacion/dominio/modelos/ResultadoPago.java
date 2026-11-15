package com.example.midliquidacion.dominio.modelos;

import lombok.Getter;

@Getter
public class ResultadoPago {
    private final boolean exitoso;
    private final String referenciaTransaccion;
    private final String motivoRechazo;


    public ResultadoPago(boolean exitoso, String referenciaTransaccion, String motivoRechazo) {
        this.exitoso = exitoso;
        this.referenciaTransaccion = referenciaTransaccion;
        this.motivoRechazo = motivoRechazo;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public String getReferencia() {
        return referenciaTransaccion;
    }
}
