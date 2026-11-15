package com.example.midliquidacion.aplicacion.puertos.puertos_salida;

import com.example.midliquidacion.dominio.modelos.ResultadoPago;
import java.math.BigDecimal;

public interface PasarelaPagosOut {
    ResultadoPago procesarPago(Long usuarioId, BigDecimal valor);
}
