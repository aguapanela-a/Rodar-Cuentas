package com.example.midliquidacion.aplicacion.casosUso;

import com.example.midliquidacion.aplicacion.puertos.puertos_salida.CrearMovimientoOut;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.PasarelaPagosOut;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.UsuariosCrudOut;
import com.example.midliquidacion.dominio.excepciones.BusinessException;
import com.example.midliquidacion.dominio.modelos.Movimiento;
import com.example.midliquidacion.dominio.modelos.ResultadoPago;
import com.example.midliquidacion.dominio.modelos.Usuario;
import com.example.midliquidacion.dominio.servicios.VerificarRecarga;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecargarSaldoImplTest {
    private final VerificarRecarga verificador = spy(new VerificarRecarga());
    private final UsuariosCrudOut usuarios = mock(UsuariosCrudOut.class);
    private final PasarelaPagosOut pasarela = mock(PasarelaPagosOut.class);
    private final CrearMovimientoOut movimientos = mock(CrearMovimientoOut.class);
    private final RecargarSaldoImpl caso = new RecargarSaldoImpl(verificador, usuarios, pasarela, movimientos);
    private final BigDecimal valor = new BigDecimal("0.01");

    @Test
    void recargaExitosaRespetaOrdenYDevuelveMovimientoGuardado() {
        when(usuarios.obtenerUsuario(1L)).thenReturn(Optional.of(new Usuario()));
        when(pasarela.procesarPago(1L, valor)).thenReturn(new ResultadoPago(true, "ref-1", null));
        ZoneId bogota = ZoneId.of("America/Bogota");
        TimeZone zonaOriginal = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Movimiento resultado;
        LocalDateTime antes;
        LocalDateTime despues;
        try {
            antes = LocalDateTime.now(bogota);
            resultado = caso.recargarSaldo(1L, valor);
            despues = LocalDateTime.now(bogota);
        } finally {
            TimeZone.setDefault(zonaOriginal);
        }

        ArgumentCaptor<Movimiento> captor = ArgumentCaptor.forClass(Movimiento.class);
        InOrder orden = inOrder(verificador, usuarios, pasarela, movimientos);
        orden.verify(verificador).verificar(valor);
        orden.verify(usuarios).obtenerUsuario(1L);
        orden.verify(pasarela).procesarPago(1L, valor);
        orden.verify(movimientos).guardar(captor.capture());
        orden.verifyNoMoreInteractions();
        assertSame(resultado, captor.getValue());
        assertEquals(1L, resultado.getUsuarioId());
        assertEquals("recarga", resultado.getTipo());
        assertEquals(valor, resultado.getMonto());
        assertEquals("ref-1", resultado.getReferencia());
        assertFalse(resultado.getCreadoEn().isBefore(antes));
        assertFalse(resultado.getCreadoEn().isAfter(despues));
    }

    @Test
    void valoresInvalidosNoInvocanPuertos() {
        for (BigDecimal invalido : new BigDecimal[]{null, BigDecimal.ZERO, new BigDecimal("-0.01")}) {
            BusinessException error = assertThrows(BusinessException.class, () -> caso.recargarSaldo(1L, invalido));
            assertEquals("400: monto invalido", error.getCodigo());
        }
        verifyNoInteractions(usuarios, pasarela, movimientos);
    }

    @Test
    void usuarioInexistenteDetieneRecarga() {
        when(usuarios.obtenerUsuario(1L)).thenReturn(Optional.empty());
        BusinessException error = assertThrows(BusinessException.class, () -> caso.recargarSaldo(1L, valor));
        assertEquals("usuario_no_encontrado", error.getCodigo());
        assertEquals("El usuario no existe", error.getMessage());
        verifyNoInteractions(pasarela, movimientos);
    }

    @Test
    void pagoRechazadoNoGuardaMovimiento() {
        when(usuarios.obtenerUsuario(1L)).thenReturn(Optional.of(new Usuario()));
        when(pasarela.procesarPago(1L, valor)).thenReturn(new ResultadoPago(false, null, "rechazado"));
        BusinessException error = assertThrows(BusinessException.class, () -> caso.recargarSaldo(1L, valor));
        assertEquals("pago_rechazado", error.getCodigo());
        assertEquals("La pasarela de pagos rechazó la transacción", error.getMessage());
        verifyNoInteractions(movimientos);
    }

    @Test
    void falloDeUsuarioSePropagaSinOperacionesPosteriores() {
        RuntimeException fallo = new RuntimeException("fallo de usuarios");
        when(usuarios.obtenerUsuario(1L)).thenThrow(fallo);
        assertSame(fallo, assertThrows(RuntimeException.class, () -> caso.recargarSaldo(1L, valor)));
        verifyNoInteractions(pasarela, movimientos);
    }

    @Test
    void falloDePagoSePropagaSinGuardar() {
        RuntimeException fallo = new RuntimeException("fallo de pago");
        when(usuarios.obtenerUsuario(1L)).thenReturn(Optional.of(new Usuario()));
        when(pasarela.procesarPago(1L, valor)).thenThrow(fallo);
        assertSame(fallo, assertThrows(RuntimeException.class, () -> caso.recargarSaldo(1L, valor)));
        verifyNoInteractions(movimientos);
    }

    @Test
    void falloDeGuardadoSePropaga() {
        RuntimeException fallo = new RuntimeException("fallo de guardado");
        when(usuarios.obtenerUsuario(1L)).thenReturn(Optional.of(new Usuario()));
        when(pasarela.procesarPago(1L, valor)).thenReturn(new ResultadoPago(true, "ref", null));
        doThrow(fallo).when(movimientos).guardar(any());
        assertSame(fallo, assertThrows(RuntimeException.class, () -> caso.recargarSaldo(1L, valor)));
        verify(movimientos).guardar(any());
    }
}