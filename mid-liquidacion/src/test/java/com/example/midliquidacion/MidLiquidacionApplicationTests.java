package com.example.midliquidacion;

import com.example.midliquidacion.aplicacion.puertos.puertos_entrada.RecargarSaldoIn;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.CrearMovimientoOut;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.PasarelaPagosOut;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.UsuariosCrudOut;
import com.example.midliquidacion.dominio.excepciones.BusinessException;
import com.example.midliquidacion.dominio.servicios.VerificarRecarga;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MidLiquidacionApplicationTests {

    @Autowired
    private ApplicationContext contexto;

    @Test
    void contextLoads() {
        assertEquals(1, contexto.getBeansOfType(VerificarRecarga.class).size());
        assertEquals(1, contexto.getBeansOfType(RecargarSaldoIn.class).size());
        assertEquals(1, contexto.getBeansOfType(UsuariosCrudOut.class).size());
        assertEquals(1, contexto.getBeansOfType(PasarelaPagosOut.class).size());
        assertEquals(1, contexto.getBeansOfType(CrearMovimientoOut.class).size());

        RecargarSaldoIn recarga = contexto.getBean(RecargarSaldoIn.class);
        assertThrows(BusinessException.class, () -> recarga.recargarSaldo(1L, BigDecimal.ZERO));
        assertThrows(UnsupportedOperationException.class, () -> recarga.recargarSaldo(1L, BigDecimal.ONE));
        assertThrows(UnsupportedOperationException.class,
                () -> contexto.getBean(UsuariosCrudOut.class).obtenerUsuario(1L));
        assertThrows(UnsupportedOperationException.class,
                () -> contexto.getBean(PasarelaPagosOut.class).procesarPago(1L, BigDecimal.ONE));
        assertThrows(UnsupportedOperationException.class,
                () -> contexto.getBean(CrearMovimientoOut.class).guardar(null));
    }

}
