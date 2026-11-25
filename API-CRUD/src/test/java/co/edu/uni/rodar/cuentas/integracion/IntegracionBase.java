package co.edu.uni.rodar.cuentas.integracion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Base de las pruebas de integración: la app completa (MockMvc) contra un PostgreSQL 16 real en Docker.
 * El contenedor se inicia una sola vez (bloque static) y lo comparten todas las clases.
 * Cada prueba crea sus propios usuarios con cédula y correo aleatorios.
 */
@SpringBootTest
@AutoConfigureMockMvc
abstract class IntegracionBase {

    @ServiceConnection
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:16");

    static {
        POSTGRES.start();
    }

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected JdbcTemplate jdbc;

    protected static String cedulaUnica() {
        return String.valueOf(ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L));
    }

    protected static String correoUnico() {
        return "prueba-" + UUID.randomUUID() + "@example.com";
    }

    protected static String referenciaUnica(String prefijo) {
        return prefijo + "-" + UUID.randomUUID();
    }

    protected ResultActions crearUsuario(String cedula, String correo) throws Exception {
        String cuerpo = """
                {"cedula": "%s", "primerNombre": "Nicolas", "primerApellido": "Castro", "correo": "%s"}
                """.formatted(cedula, correo);
        return mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content(cuerpo));
    }

    /** Crea un usuario activo (y su billetera en 0) y retorna su cédula. */
    protected String crearUsuarioActivo() throws Exception {
        String cedula = cedulaUnica();
        crearUsuario(cedula, correoUnico()).andExpect(status().isCreated());
        return cedula;
    }

    protected String crearUsuarioConSaldo(long saldo) throws Exception {
        String cedula = crearUsuarioActivo();
        if (saldo > 0) {
            registrarMovimiento(cedula, "recarga", saldo, referenciaUnica("recarga")).andExpect(status().isCreated());
        }
        return cedula;
    }

    protected ResultActions registrarMovimiento(String cedula, String tipo, long monto, String referencia)
            throws Exception {
        String cuerpo = """
                {"tipo": "%s", "monto": %d, "referencia": "%s"}
                """.formatted(tipo, monto, referencia);
        return mvc.perform(post("/usuarios/{id}/movimientos", cedula)
                .contentType(MediaType.APPLICATION_JSON).content(cuerpo));
    }

    protected ResultActions cambiarEstado(String cedula, String estado) throws Exception {
        return mvc.perform(patch("/usuarios/{id}", cedula)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\": \"" + estado + "\"}"));
    }

    /** Saldo leído directamente de la tabla, sin pasar por la API. */
    protected long saldoEnBd(String cedula) {
        return jdbc.queryForObject("select saldo from billeteras where usuario_id = ?", Long.class, cedula);
    }

    protected int movimientosDeUsuario(String cedula) {
        return jdbc.queryForObject("select count(*) from movimientos where usuario_id = ?", Integer.class, cedula);
    }

    protected int movimientosConReferencia(String referencia) {
        return jdbc.queryForObject("select count(*) from movimientos where referencia = ?", Integer.class, referencia);
    }
}
