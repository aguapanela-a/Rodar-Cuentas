package com.example.midliquidacion.dominio.modelos;

//Solo tiene los datos necesarios del API

public record ViajesResponse(
    String usuarioId,
    String zonaDestinoId,
    String estado, 
    Integer minutos
) {}