package com.example.midliquidacion.dominio.modelos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LiquidacionRequest {
    private String id;
    private String cedula;
    private String estado;
}