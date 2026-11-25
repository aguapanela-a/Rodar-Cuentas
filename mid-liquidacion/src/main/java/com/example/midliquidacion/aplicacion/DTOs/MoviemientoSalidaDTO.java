package com.example.midliquidacion.aplicacion.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MoviemientoSalidaDTO {
    private Long usuarioId;
    private String tipo;
    private BigDecimal monto;
    private String referencia;
    private LocalDateTime creadoEn;
}
