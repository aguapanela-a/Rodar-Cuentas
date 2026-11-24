package com.example.midliquidacion.dominio.modelos;

public class ResultadoPago {
    private final boolean exitoso;
    private final String url;


    public ResultadoPago(boolean exitoso, String url) {
        this.exitoso = exitoso;
        this.url = url;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public String getUrl() {
        return url;
    }
}
