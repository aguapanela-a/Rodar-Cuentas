package co.edu.uni.rodar.cuentas.aplicacion.puertos.salida;

import co.edu.uni.rodar.cuentas.dominio.modelos.FiltroUsuarios;
import co.edu.uni.rodar.cuentas.dominio.modelos.Pagina;
import co.edu.uni.rodar.cuentas.dominio.modelos.Paginacion;
import co.edu.uni.rodar.cuentas.dominio.modelos.Usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositorio {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(UUID id);

    /** Búsqueda exacta, sin importar mayúsculas. */
    Optional<Usuario> buscarPorCorreo(String correo);

    Pagina<Usuario> listar(FiltroUsuarios filtro, Paginacion paginacion);
}
