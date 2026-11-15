package com.example.midliquidacion.dominio;

import com.example.midliquidacion.dominio.excepciones.BusinessException;
import com.example.midliquidacion.dominio.modelos.Movimiento;
import com.example.midliquidacion.dominio.modelos.ResultadoPago;
import com.example.midliquidacion.dominio.servicios.VerificarRecarga;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecargaContratosTest {
    @Test
    void validaMontosPositivosYRechazaInvalidos() {
        VerificarRecarga verificador = new VerificarRecarga();
        assertTrue(verificador.verificar(new BigDecimal("0.01")));
        for (BigDecimal valor : new BigDecimal[]{null, BigDecimal.ZERO, new BigDecimal("-0.01")}) {
            BusinessException error = assertThrows(BusinessException.class, () -> verificador.verificar(valor));
            assertEquals("400: monto invalido", error.getCodigo());
            assertEquals("el monto debe ser positivo", error.getMessage());
        }
    }

    @Test
    void construyeMovimientoYConservaReferenciaDePago() {
        LocalDateTime fecha = LocalDateTime.of(2026, 1, 2, 3, 4);
        BigDecimal monto = new BigDecimal("1.25");
        Movimiento movimiento = Movimiento.builder().usuarioId(7L).tipo("recarga")
                .monto(monto).referencia("ref").creadoEn(fecha).build();
        assertEquals(7L, movimiento.getUsuarioId());
        assertEquals("recarga", movimiento.getTipo());
        assertEquals(monto, movimiento.getMonto());
        assertEquals("ref", movimiento.getReferencia());
        assertEquals(fecha, movimiento.getCreadoEn());
        assertNull(new Movimiento().getUsuarioId());
        assertEquals(monto, new Movimiento(7L, "recarga", monto, "ref", fecha).getMonto());

        ResultadoPago resultado = new ResultadoPago(true, "ref", null);
        assertTrue(resultado.isExitoso());
        assertEquals("ref", resultado.getReferenciaTransaccion());
        assertEquals(resultado.getReferenciaTransaccion(), resultado.getReferencia());
        assertNull(new ResultadoPago(false, null, "rechazado").getReferencia());
    }
}