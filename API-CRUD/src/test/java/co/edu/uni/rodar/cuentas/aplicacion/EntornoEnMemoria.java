package co.edu.uni.rodar.cuentas.aplicacion;

import co.edu.uni.rodar.cuentas.aplicacion.servicios.GestionarUsuarioService;
import co.edu.uni.rodar.cuentas.aplicacion.servicios.ObtenerSaldoService;
import co.edu.uni.rodar.cuentas.aplicacion.servicios.RegistrarMovimientoService;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.salida.memoria.BilleteraMemoriaAdapter;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.salida.memoria.MovimientoMemoriaAdapter;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.salida.memoria.UnidadDeTrabajoMemoria;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.salida.memoria.UsuarioMemoriaAdapter;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

/** Arma los servicios a mano, con los adaptadores en memoria y un reloj fijo. Sin Spring. */
final class EntornoEnMemoria {

    static final Clock RELOJ = Clock.fixed(Instant.parse("2026-01-01T12:00:00Z"), ZoneOffset.UTC);

    final UsuarioMemoriaAdapter usuarios = new UsuarioMemoriaAdapter();
    final BilleteraMemoriaAdapter billeteras = new BilleteraMemoriaAdapter();
    final MovimientoMemoriaAdapter movimientos = new MovimientoMemoriaAdapter();
    final UnidadDeTrabajoMemoria unidadDeTrabajo = new UnidadDeTrabajoMemoria();

    final GestionarUsuarioService gestionarUsuario =
            new GestionarUsuarioService(usuarios, billeteras, unidadDeTrabajo, RELOJ);
    final ObtenerSaldoService obtenerSaldo = new ObtenerSaldoService(usuarios, billeteras);
    final RegistrarMovimientoService registrarMovimiento =
            new RegistrarMovimientoService(usuarios, billeteras, movimientos, unidadDeTrabajo, RELOJ);
}
