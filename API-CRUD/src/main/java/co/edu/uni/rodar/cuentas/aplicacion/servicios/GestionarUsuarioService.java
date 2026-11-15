package co.edu.uni.rodar.cuentas.aplicacion.servicios;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.ActualizarUsuarioComando;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.CrearUsuarioComando;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.GestionarUsuarioUseCase;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.BilleteraRepositorio;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.UnidadDeTrabajo;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.UsuarioRepositorio;
import co.edu.uni.rodar.cuentas.dominio.excepciones.CorreoDuplicadoException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.UsuarioNoEncontradoException;
import co.edu.uni.rodar.cuentas.dominio.modelos.Billetera;
import co.edu.uni.rodar.cuentas.dominio.modelos.FiltroUsuarios;
import co.edu.uni.rodar.cuentas.dominio.modelos.Pagina;
import co.edu.uni.rodar.cuentas.dominio.modelos.Paginacion;
import co.edu.uni.rodar.cuentas.dominio.modelos.Usuario;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class GestionarUsuarioService implements GestionarUsuarioUseCase {

    private final UsuarioRepositorio usuarios;
    private final BilleteraRepositorio billeteras;
    private final UnidadDeTrabajo unidadDeTrabajo;
    private final Clock reloj;

    public GestionarUsuarioService(UsuarioRepositorio usuarios, BilleteraRepositorio billeteras,
                                   UnidadDeTrabajo unidadDeTrabajo, Clock reloj) {
        this.usuarios = Objects.requireNonNull(usuarios);
        this.billeteras = Objects.requireNonNull(billeteras);
        this.unidadDeTrabajo = Objects.requireNonNull(unidadDeTrabajo);
        this.reloj = Objects.requireNonNull(reloj);
    }

    @Override
    public Usuario crear(CrearUsuarioComando comando) {
        OffsetDateTime ahora = OffsetDateTime.now(reloj);
        Usuario nuevo = Usuario.crear(comando.nombre(), comando.correo(), ahora);
        return unidadDeTrabajo.ejecutar(() -> {
            if (usuarios.buscarPorCorreo(nuevo.correo()).isPresent()) {
                throw new CorreoDuplicadoException(nuevo.correo());
            }
            Usuario guardado = usuarios.guardar(nuevo);
            billeteras.guardar(Billetera.nueva(guardado.id(), ahora));
            return guardado;
        });
    }

    @Override
    public Usuario obtener(UUID id) {
        return usuarios.buscarPorId(id).orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    @Override
    public Pagina<Usuario> listar(FiltroUsuarios filtro, Paginacion paginacion) {
        return usuarios.listar(filtro == null ? FiltroUsuarios.sinFiltros() : filtro,
                paginacion == null ? Paginacion.de(null, null) : paginacion);
    }

    @Override
    public Usuario actualizar(UUID id, ActualizarUsuarioComando comando) {
        return unidadDeTrabajo.ejecutar(() -> {
            Usuario usuario = obtener(id);
            String correo = Usuario.normalizarCorreo(comando.correo());
            usuarios.buscarPorCorreo(correo)
                    .filter(otro -> !otro.id().equals(id))
                    .ifPresent(otro -> {
                        throw new CorreoDuplicadoException(correo);
                    });
            usuario.actualizar(comando.nombre(), correo, comando.estado());
            return usuarios.guardar(usuario);
        });
    }

    @Override
    public void desactivar(UUID id) {
        unidadDeTrabajo.ejecutar(() -> {
            Usuario usuario = obtener(id);
            usuario.desactivar();
            return usuarios.guardar(usuario);
        });
    }
}
