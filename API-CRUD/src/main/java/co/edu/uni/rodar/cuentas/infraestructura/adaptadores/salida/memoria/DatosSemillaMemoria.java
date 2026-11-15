package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.salida.memoria;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.CrearUsuarioComando;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.GestionarUsuarioUseCase;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.RegistrarMovimientoComando;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.RegistrarMovimientoUseCase;
import co.edu.uni.rodar.cuentas.dominio.modelos.TipoMovimiento;
import co.edu.uni.rodar.cuentas.dominio.modelos.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * TEMPORAL (perfil "memoria"). Carga datos de prueba al arrancar. Usa los casos de uso,
 * no los mapas, para que los datos cumplan las mismas reglas que cualquier petición.
 */
@Component
@Profile("memoria")
public class DatosSemillaMemoria implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosSemillaMemoria.class);

    private final GestionarUsuarioUseCase usuarios;
    private final RegistrarMovimientoUseCase movimientos;

    public DatosSemillaMemoria(GestionarUsuarioUseCase usuarios, RegistrarMovimientoUseCase movimientos) {
        this.usuarios = usuarios;
        this.movimientos = movimientos;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 1. Activo con saldo 0 (no se le hace recarga: el monto debe ser > 0).
        Usuario sinSaldo = usuarios.crear(new CrearUsuarioComando("Ana Gómez", "ana@example.com"));

        // 2. Activo con saldo 20000.
        Usuario conSaldo = usuarios.crear(new CrearUsuarioComando("Bruno Díaz", "bruno@example.com"));
        recargar(conSaldo, 20_000, "seed-2");

        // 3. Inactivo: se recarga mientras está activo y luego se desactiva.
        Usuario inactivo = usuarios.crear(new CrearUsuarioComando("Carla Ruiz", "carla@example.com"));
        recargar(inactivo, 5_000, "seed-3");
        usuarios.desactivar(inactivo.id());

        log.info("Datos semilla (perfil memoria):");
        log.info("  saldo 0      -> {} ({})", sinSaldo.id(), sinSaldo.correo());
        log.info("  saldo 20000  -> {} ({})", conSaldo.id(), conSaldo.correo());
        log.info("  inactivo     -> {} ({})", inactivo.id(), inactivo.correo());
    }

    private void recargar(Usuario usuario, long monto, String referencia) {
        movimientos.registrar(new RegistrarMovimientoComando(usuario.id(), TipoMovimiento.RECARGA, monto,
                referencia, "Recarga inicial (datos semilla)"));
    }
}
