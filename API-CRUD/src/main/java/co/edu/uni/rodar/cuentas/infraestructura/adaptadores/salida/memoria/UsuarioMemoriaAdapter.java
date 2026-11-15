package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.salida.memoria;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.UsuarioRepositorio;
import co.edu.uni.rodar.cuentas.dominio.modelos.FiltroUsuarios;
import co.edu.uni.rodar.cuentas.dominio.modelos.Pagina;
import co.edu.uni.rodar.cuentas.dominio.modelos.Paginacion;
import co.edu.uni.rodar.cuentas.dominio.modelos.Usuario;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TEMPORAL (perfil "memoria"). Guarda copias de los usuarios para imitar a una base de datos:
 * un cambio solo queda "persistido" cuando se llama a guardar().
 */
@Component
@Profile("memoria")
public class UsuarioMemoriaAdapter implements UsuarioRepositorio {

    private final Map<UUID, Usuario> usuarios = new ConcurrentHashMap<>();

    @Override
    public Usuario guardar(Usuario usuario) {
        usuarios.put(usuario.id(), copiar(usuario));
        return copiar(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return Optional.ofNullable(usuarios.get(id)).map(UsuarioMemoriaAdapter::copiar);
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        if (correo == null) {
            return Optional.empty();
        }
        String buscado = correo.strip();
        return usuarios.values().stream()
                .filter(u -> u.correo().equalsIgnoreCase(buscado))
                .findFirst()
                .map(UsuarioMemoriaAdapter::copiar);
    }

    @Override
    public Pagina<Usuario> listar(FiltroUsuarios filtro, Paginacion paginacion) {
        List<Usuario> filtrados = usuarios.values().stream()
                .filter(filtro::cumple)
                .sorted(Comparator.comparing(Usuario::creadoEn).thenComparing(Usuario::id))
                .toList();
        List<Usuario> datos = filtrados.stream()
                .skip(paginacion.desplazamiento())
                .limit(paginacion.tamano())
                .map(UsuarioMemoriaAdapter::copiar)
                .toList();
        return new Pagina<>(datos, paginacion.pagina(), paginacion.tamano(), filtrados.size());
    }

    private static Usuario copiar(Usuario u) {
        return new Usuario(u.id(), u.nombre(), u.correo(), u.estado(), u.creadoEn());
    }
}
