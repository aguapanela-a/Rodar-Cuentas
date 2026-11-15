package co.edu.uni.rodar.cuentas.dominio;

import co.edu.uni.rodar.cuentas.dominio.excepciones.DatosInvalidosException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.SaldoInsuficienteException;
import co.edu.uni.rodar.cuentas.dominio.modelos.Billetera;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BilleteraTest {

    private static final OffsetDateTime AHORA = OffsetDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

    @Test
    void debitarMasQueElSaldoLanzaExcepcionYNoCambiaElSaldo() {
        Billetera billetera = Billetera.nueva(UUID.randomUUID(), AHORA);
        billetera.recargar(3_000, AHORA);

        SaldoInsuficienteException ex = assertThrows(SaldoInsuficienteException.class,
                () -> billetera.debitar(6_240, AHORA.plusMinutes(1)));

        assertEquals(3_000, billetera.saldo());
        assertEquals(AHORA, billetera.actualizadoEn());
        assertEquals("saldo_insuficiente", ex.codigo());
        assertEquals(3_000L, ex.detalle().get("saldo"));
        assertEquals(6_240L, ex.detalle().get("monto"));
    }

    @Test
    void debitarExactamenteElSaldoLoDejaEnCero() {
        Billetera billetera = Billetera.nueva(UUID.randomUUID(), AHORA);
        billetera.recargar(5_000, AHORA);

        billetera.debitar(5_000, AHORA);

        assertEquals(0, billetera.saldo());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1, -20_000})
    void recargaConMontoMenorOIgualACeroEsInvalida(long monto) {
        Billetera billetera = Billetera.nueva(UUID.randomUUID(), AHORA);

        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class,
                () -> billetera.recargar(monto, AHORA));

        assertEquals("monto", ex.detalle().get("campo"));
        assertEquals(0, billetera.saldo());
    }
}
