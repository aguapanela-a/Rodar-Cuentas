package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.salida.persistencia;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.UsuarioRepositorio;
import co.edu.uni.rodar.cuentas.dominio.modelos.FiltroUsuarios;
import co.edu.uni.rodar.cuentas.dominio.modelos.Pagina;
import co.edu.uni.rodar.cuentas.dominio.modelos.Paginacion;
import co.edu.uni.rodar.cuentas.dominio.modelos.Usuario;

import java.util.Optional;
import java.util.UUID;

/** Pendiente: se implementa con PostgreSQL en la rama de base de datos. No es un bean todavía. */
public class UsuarioPersistenceAdapter implements UsuarioRepositorio {

    @Override
    public Usuario guardar(Usuario usuario) {
        throw new UnsupportedOperationException("Pendiente: rama de base de datos");
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        throw new UnsupportedOperationException("Pendiente: rama de base de datos");
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        throw new UnsupportedOperationException("Pendiente: rama de base de datos");
    }

    @Override
    public Pagina<Usuario> listar(FiltroUsuarios filtro, Paginacion paginacion) {
        throw new UnsupportedOperationException("Pendiente: rama de base de datos");
    }
}
