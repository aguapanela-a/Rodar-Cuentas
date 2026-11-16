package com.example.midliquidacion.aplicacion.puertos.puertos_entrada;

import com.example.midliquidacion.aplicacion.DTOs.MoviemientoSalidaDTO;

import java.math.BigDecimal;

public interface CrearMovimientoIn {
    MoviemientoSalidaDTO crearMovimiento(Long usuarioId, String tipoMovimiento, String referencia, BigDecimal monto);
}
