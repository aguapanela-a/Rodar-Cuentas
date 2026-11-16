package com.example.midliquidacion.aplicacion.casosUso;

import com.example.midliquidacion.aplicacion.puertos.puertos_salida.PasarelaPagosOut;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.UsuariosCrudOut;
import com.example.midliquidacion.dominio.excepciones.BusinessException;
import com.example.midliquidacion.dominio.modelos.ResultadoPago;
import com.example.midliquidacion.dominio.modelos.Usuario;
import com.example.midliquidacion.dominio.servicios.VerificarRecarga;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecargarSaldoImplTest {
    private final VerificarRecarga verificador = spy(new VerificarRecarga());
    private final UsuariosCrudOut usuarios = mock(UsuariosCrudOut.class);
    private final PasarelaPagosOut pasarela = mock(PasarelaPagosOut.class);
    private final RecargarSaldoImpl caso = new RecargarSaldoImpl(verificador, usuarios, pasarela);
    private final BigDecimal valor = new BigDecimal("1000.00");

    @Test
    void recargaExitosaRespetaOrdenYDevuelveUrlSinGuardarMovimiento() {
        when(usuarios.obtenerUsuario(1L)).thenReturn(Optional.of(new Usuario()));
        when(pasarela.procesarPago(1L, valor)).thenReturn(new ResultadoPago(true, "https://checkout.example/sesion"));
        String resultado = caso.recargarSaldo(1L, valor);

        InOrder orden = inOrder(verificador, usuarios, pasarela);
        orden.verify(verificador).verificar(valor);
        orden.verify(usuarios).obtenerUsuario(1L);
        orden.verify(pasarela).procesarPago(1L, valor);
        orden.verifyNoMoreInteractions();
        assertEquals("https://checkout.example/sesion", resultado);
    }

    @Test
    void valoresInvalidosNoInvocanPuertos() {
        for (BigDecimal invalido : new BigDecimal[]{null, BigDecimal.ZERO, new BigDecimal("-0.01"), new BigDecimal("1.01")}) {
            BusinessException error = assertThrows(BusinessException.class, () -> caso.recargarSaldo(1L, invalido));
            assertEquals("400: monto invalido", error.getCodigo());
        }
        verifyNoInteractions(usuarios, pasarela);
    }

    @Test
    void usuarioInexistenteDetieneRecarga() {
        when(usuarios.obtenerUsuario(1L)).thenReturn(Optional.empty());
        BusinessException error = assertThrows(BusinessException.class, () -> caso.recargarSaldo(1L, valor));
        assertEquals("usuario_no_encontrado", error.getCodigo());
        assertEquals("El usuario no existe", error.getMessage());
        verifyNoInteractions(pasarela);
    }

    @Test
    void pagoRechazadoNoGuardaMovimiento() {
        when(usuarios.obtenerUsuario(1L)).thenReturn(Optional.of(new Usuario()));
        when(pasarela.procesarPago(1L, valor)).thenReturn(new ResultadoPago(false, "https://checkout.example/sesion"));
        BusinessException error = assertThrows(BusinessException.class, () -> caso.recargarSaldo(1L, valor));
        assertEquals("pago_rechazado", error.getCodigo());
        assertEquals("La pasarela de pagos rechazó la transacción", error.getMessage());
    }

    @Test
    void falloDeUsuarioSePropagaSinOperacionesPosteriores() {
        RuntimeException fallo = new RuntimeException("fallo de usuarios");
        when(usuarios.obtenerUsuario(1L)).thenThrow(fallo);
        assertSame(fallo, assertThrows(RuntimeException.class, () -> caso.recargarSaldo(1L, valor)));
        verifyNoInteractions(pasarela);
    }

    @Test
    void falloDePagoSePropagaSinGuardar() {
        RuntimeException fallo = new RuntimeException("fallo de pago");
        when(usuarios.obtenerUsuario(1L)).thenReturn(Optional.of(new Usuario()));
        when(pasarela.procesarPago(1L, valor)).thenThrow(fallo);
        assertSame(fallo, assertThrows(RuntimeException.class, () -> caso.recargarSaldo(1L, valor)));
    }

    @Test
    void resultadoAusenteOSinUrlSeRechaza() {
        when(usuarios.obtenerUsuario(1L)).thenReturn(Optional.of(new Usuario()));
        for (ResultadoPago resultado : new ResultadoPago[]{null, new ResultadoPago(true, null),
                new ResultadoPago(true, ""), new ResultadoPago(true, "  \t")}) {
            when(pasarela.procesarPago(1L, valor)).thenReturn(resultado);
            BusinessException error = assertThrows(BusinessException.class, () -> caso.recargarSaldo(1L, valor));
            assertEquals("pago_rechazado", error.getCodigo());
        }
    }
}