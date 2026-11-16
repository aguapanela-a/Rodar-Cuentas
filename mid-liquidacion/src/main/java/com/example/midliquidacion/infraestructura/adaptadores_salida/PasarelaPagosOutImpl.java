package com.example.midliquidacion.infraestructura.adaptadores_salida;

import com.example.midliquidacion.aplicacion.puertos.puertos_salida.PasarelaPagosOut;
import com.example.midliquidacion.dominio.modelos.ResultadoPago;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PasarelaPagosOutImpl implements PasarelaPagosOut {
    @Override
    public ResultadoPago procesarPago(Long usuarioId, BigDecimal valor) {
        // TODO: Implementar adaptador de salida en infraestructura



        throw new UnsupportedOperationException("Adaptador de pagos pendiente de implementación");
    }
}
