package com.example.midliquidacion.aplicacion.casosUso;

import com.example.midliquidacion.aplicacion.puertos.puertos_entrada.RecargarSaldoIn;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.PasarelaPagosOut;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.UsuariosCrudOut;
import com.example.midliquidacion.dominio.excepciones.BusinessException;
import com.example.midliquidacion.dominio.modelos.ResultadoPago;
import com.example.midliquidacion.dominio.servicios.VerificarRecarga;
import java.math.BigDecimal;

public class RecargarSaldoImpl implements RecargarSaldoIn {
    private final VerificarRecarga verificarRecarga;
    private final UsuariosCrudOut usuariosCrud;
    private final PasarelaPagosOut pasarelaPagos;
    public RecargarSaldoImpl(VerificarRecarga verificarRecarga, UsuariosCrudOut usuariosCrud,
                             PasarelaPagosOut pasarelaPagos) {
        this.verificarRecarga = verificarRecarga;
        this.usuariosCrud = usuariosCrud;
        this.pasarelaPagos = pasarelaPagos;
    }


    // finalmente retorna la url de la pasarela de pagos para que el front redirija al usuario
    @Override
    public String recargarSaldo(Long usuarioId, BigDecimal valor) {
        // llama nucleo para que verifique el valor de la recarga
        verificarRecarga.verificar(valor);

        // llama a puerto de salida de ususarios para verificar existencia
        usuariosCrud.obtenerUsuario(usuarioId)
                .orElseThrow(() -> new BusinessException("usuario_no_encontrado", "El usuario no existe"));

        // llama a puerto de salida de pagos para procesar el pago
        ResultadoPago resultado = pasarelaPagos.procesarPago(usuarioId, valor);
        if (resultado == null || !resultado.isExitoso() || resultado.getUrl() == null || resultado.getUrl().isBlank()) {
            throw new BusinessException("pago_rechazado", "La pasarela de pagos rechazó la transacción");
        }
        return resultado.getUrl();
    }
}
