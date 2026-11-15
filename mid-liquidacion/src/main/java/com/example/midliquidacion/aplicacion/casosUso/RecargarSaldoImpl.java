package com.example.midliquidacion.aplicacion.casosUso;

import com.example.midliquidacion.aplicacion.puertos.puertos_entrada.RecargarSaldoIn;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.CrearMovimientoOut;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.PasarelaPagosOut;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.UsuariosCrudOut;
import com.example.midliquidacion.dominio.excepciones.BusinessException;
import com.example.midliquidacion.dominio.modelos.Movimiento;
import com.example.midliquidacion.dominio.modelos.ResultadoPago;
import com.example.midliquidacion.dominio.servicios.VerificarRecarga;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class RecargarSaldoImpl implements RecargarSaldoIn {
    private final VerificarRecarga verificarRecarga;
    private final UsuariosCrudOut usuariosCrud;
    private final PasarelaPagosOut pasarelaPagos;
    private final CrearMovimientoOut crearMovimiento;

    public RecargarSaldoImpl(VerificarRecarga verificarRecarga, UsuariosCrudOut usuariosCrud,
                             PasarelaPagosOut pasarelaPagos, CrearMovimientoOut crearMovimiento) {
        this.verificarRecarga = verificarRecarga;
        this.usuariosCrud = usuariosCrud;
        this.pasarelaPagos = pasarelaPagos;
        this.crearMovimiento = crearMovimiento;
    }

    @Override
    public Movimiento recargarSaldo(Long usuarioId, BigDecimal valor) {
        // llama nucleo para que verifique el valor de la recarga
        verificarRecarga.verificar(valor);

        // llama a puerto de salida de ususarios para verificar existencia
        usuariosCrud.obtenerUsuario(usuarioId)
                .orElseThrow(() -> new BusinessException("usuario_no_encontrado", "El usuario no existe"));

        // llama a puerto de salida de pagos para procesar el pago
        ResultadoPago resultado = pasarelaPagos.procesarPago(usuarioId, valor);
        if (!resultado.isExitoso()) {
            throw new BusinessException("pago_rechazado", "La pasarela de pagos rechazó la transacción");
        }

        // crea el movimiento con la clase pura del dominio
        Movimiento movimiento = Movimiento.builder()
                .usuarioId(usuarioId)
                .tipo("recarga")
                .monto(valor)
                .referencia(resultado.getReferencia())
                .creadoEn(LocalDateTime.now(ZoneId.of("America/Bogota")))
                .build();

        // llama a puerto de salida de movimientos para guardar el movimiento
        crearMovimiento.guardar(movimiento);
        return movimiento;
    }
}
