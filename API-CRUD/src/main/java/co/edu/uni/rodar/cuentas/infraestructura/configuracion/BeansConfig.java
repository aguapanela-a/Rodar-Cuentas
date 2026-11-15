package co.edu.uni.rodar.cuentas.infraestructura.configuracion;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.ConsultarMovimientosUseCase;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.GestionarUsuarioUseCase;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.ObtenerSaldoUseCase;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.RegistrarMovimientoUseCase;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.BilleteraRepositorio;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.MovimientoRepositorio;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.UnidadDeTrabajo;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.UsuarioRepositorio;
import co.edu.uni.rodar.cuentas.aplicacion.servicios.ConsultarMovimientosService;
import co.edu.uni.rodar.cuentas.aplicacion.servicios.GestionarUsuarioService;
import co.edu.uni.rodar.cuentas.aplicacion.servicios.ObtenerSaldoService;
import co.edu.uni.rodar.cuentas.aplicacion.servicios.RegistrarMovimientoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Registra los servicios de aplicación como beans. Los servicios son Java puro (sin @Service),
 * así que Spring solo los conoce por aquí. Cada bean se expone con el tipo de su caso de uso,
 * para que los controladores dependan del puerto y no de la implementación.
 */
@Configuration
public class BeansConfig {

    @Bean
    public Clock reloj() {
        return Clock.systemUTC();
    }

    @Bean
    public GestionarUsuarioUseCase gestionarUsuarioUseCase(UsuarioRepositorio usuarios,
                                                           BilleteraRepositorio billeteras,
                                                           UnidadDeTrabajo unidadDeTrabajo, Clock reloj) {
        return new GestionarUsuarioService(usuarios, billeteras, unidadDeTrabajo, reloj);
    }

    @Bean
    public ObtenerSaldoUseCase obtenerSaldoUseCase(UsuarioRepositorio usuarios, BilleteraRepositorio billeteras) {
        return new ObtenerSaldoService(usuarios, billeteras);
    }

    @Bean
    public RegistrarMovimientoUseCase registrarMovimientoUseCase(UsuarioRepositorio usuarios,
                                                                 BilleteraRepositorio billeteras,
                                                                 MovimientoRepositorio movimientos,
                                                                 UnidadDeTrabajo unidadDeTrabajo, Clock reloj) {
        return new RegistrarMovimientoService(usuarios, billeteras, movimientos, unidadDeTrabajo, reloj);
    }

    @Bean
    public ConsultarMovimientosUseCase consultarMovimientosUseCase(UsuarioRepositorio usuarios,
                                                                   MovimientoRepositorio movimientos) {
        return new ConsultarMovimientosService(usuarios, movimientos);
    }
}
