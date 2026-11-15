package co.edu.uni.rodar.cuentas.integracion;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MovimientosIntegracionTest extends IntegracionBase {

    // ---------- C-HU1: el monto debe ser mayor que cero ----------

    @Test
    void chu1MontoCeroONegativoResponde400YNoCambiaNada() throws Exception {
        String cedula = crearUsuarioConSaldo(10_000);

        registrarMovimiento(cedula, "recarga", 0, referenciaUnica("cero"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("solicitud_invalida"))
                .andExpect(jsonPath("$.detalle.campo").value("monto"));
        registrarMovimiento(cedula, "debito", -500, referenciaUnica("negativo"))
                .andExpect(status().isBadRequest());

        assertThat(saldoEnBd(cedula)).isEqualTo(10_000);
        assertThat(movimientosDeUsuario(cedula)).isEqualTo(1);
    }

    // ---------- C-HU2: un débito nunca deja el saldo negativo ----------

    @Test
    void chu2DebitoMayorAlSaldoResponde409YNoRegistraNada() throws Exception {
        String cedula = crearUsuarioConSaldo(3_000);
        String referencia = referenciaUnica("via");

        registrarMovimiento(cedula, "debito", 6_240, referencia)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("saldo_insuficiente"))
                .andExpect(jsonPath("$.detalle.saldo").value(3_000))
                .andExpect(jsonPath("$.detalle.monto").value(6_240));

        assertThat(saldoEnBd(cedula)).isEqualTo(3_000);
        assertThat(movimientosConReferencia(referencia)).isZero();
    }

    @Test
    void debitoIgualAlSaldoDejaElSaldoEnCero() throws Exception {
        String cedula = crearUsuarioConSaldo(5_000);

        registrarMovimiento(cedula, "debito", 5_000, referenciaUnica("via"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("debito"));

        assertThat(saldoEnBd(cedula)).isZero();
    }

    // ---------- C-HU3: idempotencia por referencia ----------

    @Test
    void chu3MismaReferenciaDevuelveElOriginalCon200SinTocarElSaldo() throws Exception {
        String cedula = crearUsuarioConSaldo(20_000);
        String referencia = referenciaUnica("via");

        String primera = registrarMovimiento(cedula, "debito", 6_240, referencia)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String idOriginal = JsonPath.read(primera, "$.id");

        registrarMovimiento(cedula, "debito", 6_240, referencia)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idOriginal));

        assertThat(saldoEnBd(cedula)).isEqualTo(20_000 - 6_240);
        assertThat(movimientosConReferencia(referencia)).isEqualTo(1);
    }

    @Test
    void chu3MismaReferenciaConOtrosDatosResponde409() throws Exception {
        String cedula = crearUsuarioConSaldo(20_000);
        String otroUsuario = crearUsuarioConSaldo(20_000);
        String referencia = referenciaUnica("via");
        registrarMovimiento(cedula, "debito", 6_240, referencia).andExpect(status().isCreated());

        registrarMovimiento(cedula, "debito", 1_000, referencia)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("referencia_en_conflicto"));
        registrarMovimiento(cedula, "recarga", 6_240, referencia)
                .andExpect(status().isConflict());
        registrarMovimiento(otroUsuario, "debito", 6_240, referencia)
                .andExpect(status().isConflict());

        assertThat(saldoEnBd(cedula)).isEqualTo(20_000 - 6_240);
        assertThat(saldoEnBd(otroUsuario)).isEqualTo(20_000);
    }

    // ---------- Usuario inactivo / inexistente ----------

    @Test
    void usuarioInactivoNoPuedeRegistrarMovimientos() throws Exception {
        String cedula = crearUsuarioConSaldo(10_000);
        cambiarEstado(cedula, "inactivo").andExpect(status().isOk());

        registrarMovimiento(cedula, "recarga", 5_000, referenciaUnica("recarga"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("usuario_inactivo"));
        registrarMovimiento(cedula, "debito", 1_000, referenciaUnica("via"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("usuario_inactivo"));

        assertThat(saldoEnBd(cedula)).isEqualTo(10_000);
        assertThat(movimientosDeUsuario(cedula)).isEqualTo(1);

        // El historial y la billetera siguen disponibles aunque esté inactivo.
        mvc.perform(get("/usuarios/{id}/movimientos", cedula)).andExpect(status().isOk());
        mvc.perform(get("/usuarios/{id}/billetera", cedula)).andExpect(status().isOk());
    }

    @Test
    void usuarioInexistenteResponde404() throws Exception {
        registrarMovimiento("999", "recarga", 5_000, referenciaUnica("recarga"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("usuario_no_encontrado"));
        mvc.perform(get("/usuarios/{id}/movimientos", "999"))
                .andExpect(status().isNotFound());
    }

    // ---------- Historial ----------

    @Test
    void historialMuestraLaCedulaUnaVezYLosMovimientosMasRecientesPrimero() throws Exception {
        String cedula = crearUsuarioConSaldo(10_000);
        String referenciaDebito = referenciaUnica("via");
        registrarMovimiento(cedula, "debito", 2_500, referenciaDebito).andExpect(status().isCreated());

        mvc.perform(get("/usuarios/{id}/movimientos", cedula).param("tamano", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cedula").value(cedula))
                .andExpect(jsonPath("$.pagina").value(1))
                .andExpect(jsonPath("$.tamano").value(1))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.datos.length()").value(1))
                .andExpect(jsonPath("$.datos[0].referencia").value(referenciaDebito))
                .andExpect(jsonPath("$.datos[0].tipo").value("debito"))
                .andExpect(jsonPath("$.datos[0].monto").value(2_500))
                .andExpect(jsonPath("$.datos[0].cedula").doesNotExist());

        assertThat(saldoEnBd(cedula)).isEqualTo(7_500);
    }
}
