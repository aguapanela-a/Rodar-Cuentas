package co.edu.uni.rodar.cuentas.aplicacion;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.ActualizarUsuarioComando;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.CrearUsuarioComando;
import co.edu.uni.rodar.cuentas.dominio.excepciones.CorreoDuplicadoException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.UsuarioNoEncontradoException;
import co.edu.uni.rodar.cuentas.dominio.modelos.Billetera;
import co.edu.uni.rodar.cuentas.dominio.modelos.EstadoUsuario;
import co.edu.uni.rodar.cuentas.dominio.modelos.FiltroUsuarios;
import co.edu.uni.rodar.cuentas.dominio.modelos.Paginacion;
import co.edu.uni.rodar.cuentas.dominio.modelos.Usuario;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GestionarUsuarioServiceTest {

    private final EntornoEnMemoria entorno = new EntornoEnMemoria();

    @Test
    void crearUsuarioCreaSuBilleteraConSaldoCero() {
        Usuario usuario = entorno.gestionarUsuario.crear(new CrearUsuarioComando("Ana Gómez", "ana@example.com"));

        Billetera billetera = entorno.obtenerSaldo.obtener(usuario.id());
        assertEquals(0, billetera.saldo());
        assertEquals(OffsetDateTime.now(EntornoEnMemoria.RELOJ), billetera.actualizadoEn());
    }

    @Test
    void correoDuplicadoSinImportarMayusculasEsRechazado() {
        entorno.gestionarUsuario.crear(new CrearUsuarioComando("Ana Gómez", "ana@example.com"));

        CorreoDuplicadoException ex = assertThrows(CorreoDuplicadoException.class,
                () -> entorno.gestionarUsuario.crear(new CrearUsuarioComando("Otra Ana", "  ANA@Example.com ")));

        assertEquals("ana@example.com", ex.detalle().get("correo"));
        assertEquals(1, entorno.gestionarUsuario.listar(FiltroUsuarios.sinFiltros(), Paginacion.de(null, null)).total());
    }

    @Test
    void actualizarConElCorreoDeOtroUsuarioEsRechazado() {
        entorno.gestionarUsuario.crear(new CrearUsuarioComando("Ana Gómez", "ana@example.com"));
        Usuario bruno = entorno.gestionarUsuario.crear(new CrearUsuarioComando("Bruno Díaz", "bruno@example.com"));

        assertThrows(CorreoDuplicadoException.class, () -> entorno.gestionarUsuario.actualizar(bruno.id(),
                new ActualizarUsuarioComando("Bruno Díaz", "ANA@example.com", EstadoUsuario.ACTIVO)));
    }

    @Test
    void actualizarConservandoSuPropioCorreoEsValido() {
        Usuario ana = entorno.gestionarUsuario.crear(new CrearUsuarioComando("Ana Gómez", "ana@example.com"));

        Usuario actualizado = entorno.gestionarUsuario.actualizar(ana.id(),
                new ActualizarUsuarioComando("Ana María Gómez", "Ana@Example.com", EstadoUsuario.ACTIVO));

        assertEquals("Ana María Gómez", actualizado.nombre());
    }

    @Test
    void desactivarEsBorradoLogico() {
        Usuario ana = entorno.gestionarUsuario.crear(new CrearUsuarioComando("Ana Gómez", "ana@example.com"));

        entorno.gestionarUsuario.desactivar(ana.id());

        assertEquals(EstadoUsuario.INACTIVO, entorno.gestionarUsuario.obtener(ana.id()).estado());
    }

    @Test
    void obtenerUsuarioInexistenteLanzaNoEncontrado() {
        assertThrows(UsuarioNoEncontradoException.class, () -> entorno.gestionarUsuario.obtener(UUID.randomUUID()));
    }
}
