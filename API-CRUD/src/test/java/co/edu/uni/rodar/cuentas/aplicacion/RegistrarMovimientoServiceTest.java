package co.edu.uni.rodar.cuentas.aplicacion;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.CrearUsuarioComando;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.RegistrarMovimientoComando;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.ResultadoMovimiento;
import co.edu.uni.rodar.cuentas.dominio.excepciones.ReferenciaEnConflictoException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.SaldoInsuficienteException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.UsuarioInactivoException;
import co.edu.uni.rodar.cuentas.dominio.modelos.FiltroMovimientos;
import co.edu.uni.rodar.cuentas.dominio.modelos.Paginacion;
import co.edu.uni.rodar.cuentas.dominio.modelos.TipoMovimiento;
import co.edu.uni.rodar.cuentas.dominio.modelos.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static co.edu.uni.rodar.cuentas.dominio.modelos.TipoMovimiento.DEBITO;
import static co.edu.uni.rodar.cuentas.dominio.modelos.TipoMovimiento.RECARGA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistrarMovimientoServiceTest {

    private final EntornoEnMemoria entorno = new EntornoEnMemoria();
    private Usuario usuario;

    @BeforeEach
    void crearUsuarioConSaldo() {
        usuario = entorno.gestionarUsuario.crear(new CrearUsuarioComando("Ana Gómez", "ana@example.com"));
        registrar(RECARGA, 20_000, "recarga-1");
    }

    @Test
    void mismaReferenciaDevuelveElMismoMovimientoSinDobleCobro() {
        ResultadoMovimiento primero = registrar(DEBITO, 6_240, "via-0042");
        ResultadoMovimiento reintento = registrar(DEBITO, 6_240, "via-0042");

        assertFalse(primero.yaExistia());
        assertTrue(reintento.yaExistia());
        assertEquals(primero.movimiento().id(), reintento.movimiento().id());
        assertEquals(13_760, saldo());
        assertEquals(2, totalMovimientos());
    }

    @Test
    void referenciaRepetidaConDatosDistintosEsConflicto() {
        registrar(DEBITO, 6_240, "via-0042");

        ReferenciaEnConflictoException ex = assertThrows(ReferenciaEnConflictoException.class,
                () -> registrar(DEBITO, 9_999, "via-0042"));

        assertEquals("via-0042", ex.detalle().get("referencia"));
        assertEquals(13_760, saldo());
    }

    @Test
    void usuarioInactivoNoPuedeRegistrarMovimientos() {
        entorno.gestionarUsuario.desactivar(usuario.id());

        assertThrows(UsuarioInactivoException.class, () -> registrar(RECARGA, 1_000, "recarga-2"));
        assertThrows(UsuarioInactivoException.class, () -> registrar(DEBITO, 1_000, "via-0043"));
        assertEquals(20_000, saldo());
    }

    @Test
    void debitoMayorAlSaldoNoCambiaNadaYNoRegistraMovimiento() {
        assertThrows(SaldoInsuficienteException.class, () -> registrar(DEBITO, 25_000, "via-0044"));

        assertEquals(20_000, saldo());
        assertEquals(1, totalMovimientos());
    }

    @Test
    void elMovimientoGuardaElSaldoResultante() {
        ResultadoMovimiento resultado = registrar(DEBITO, 6_240, "via-0045");

        assertEquals(13_760, resultado.movimiento().saldoResultante());
    }

    private ResultadoMovimiento registrar(TipoMovimiento tipo, long monto, String referencia) {
        return entorno.registrarMovimiento.registrar(
                new RegistrarMovimientoComando(usuario.id(), tipo, monto, referencia, null));
    }

    private long saldo() {
        return entorno.obtenerSaldo.obtener(usuario.id()).saldo();
    }

    private long totalMovimientos() {
        return entorno.movimientos.listar(usuario.id(), FiltroMovimientos.sinFiltros(), Paginacion.de(null, null)).total();
    }
}
