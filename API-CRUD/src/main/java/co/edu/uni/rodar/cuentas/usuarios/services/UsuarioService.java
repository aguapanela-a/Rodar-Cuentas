package co.edu.uni.rodar.cuentas.usuarios.services;

import co.edu.uni.rodar.cuentas.billeteras.services.BilleteraService;
import co.edu.uni.rodar.cuentas.common.dto.PaginaResponse;
import co.edu.uni.rodar.cuentas.common.exceptions.ConflictoException;
import co.edu.uni.rodar.cuentas.common.exceptions.RecursoNoEncontradoException;
import co.edu.uni.rodar.cuentas.common.exceptions.SolicitudInvalidaException;
import co.edu.uni.rodar.cuentas.common.exceptions.UsuarioInactivoException;
import co.edu.uni.rodar.cuentas.common.util.TextoEnum;
import co.edu.uni.rodar.cuentas.usuarios.dto.ActualizarUsuarioRequest;
import co.edu.uni.rodar.cuentas.usuarios.dto.CrearUsuarioRequest;
import co.edu.uni.rodar.cuentas.usuarios.dto.EstadoUsuarioDto;
import co.edu.uni.rodar.cuentas.usuarios.dto.UsuarioResponse;
import co.edu.uni.rodar.cuentas.usuarios.models.EstadoUsuario;
import co.edu.uni.rodar.cuentas.usuarios.models.Usuario;
import co.edu.uni.rodar.cuentas.usuarios.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Sort ORDEN_LISTADO = Sort.by("cedula");

    private final UsuarioRepository usuarioRepository;
    private final BilleteraService billeteraService;

    /** Crea el usuario ACTIVO y su billetera en 0, en la misma transacción. */
    @Transactional
    public UsuarioResponse crear(CrearUsuarioRequest solicitud) {
        String cedula = obligatorio(solicitud.cedula(), "cedula");
        String correo = normalizarCorreo(obligatorio(solicitud.correo(), "correo"));

        if (usuarioRepository.existsById(cedula)) {
            throw new ConflictoException("cedula_duplicada", "Ya existe un usuario con esa cédula",
                    Map.of("cedula", cedula));
        }
        validarCorreoLibre(correo, null);

        Usuario usuario = new Usuario();
        usuario.setCedula(cedula);
        asignarDatos(usuario, solicitud.primerNombre(), solicitud.segundoNombre(),
                solicitud.primerApellido(), solicitud.segundoApellido(), correo);
        usuario.setEstado(EstadoUsuario.ACTIVO);

        Usuario guardado = usuarioRepository.saveAndFlush(usuario);
        billeteraService.crear(guardado);
        return UsuarioResponse.desde(guardado);
    }

    /** Filtros opcionales: estado y correo (exacto). La página empieza en 1. */
    @Transactional(readOnly = true)
    public PaginaResponse<UsuarioResponse> listar(String estado, String correo, int pagina, int tamano) {
        EstadoUsuario filtroEstado = vacio(estado) ? null : TextoEnum.desdeTexto(EstadoUsuario.class, estado, "estado");

        if (!vacio(correo)) {
            // El correo es único: el resultado tiene 0 o 1 usuario.
            List<UsuarioResponse> encontrados = usuarioRepository.findByCorreo(normalizarCorreo(correo))
                    .filter(usuario -> filtroEstado == null || usuario.getEstado() == filtroEstado)
                    .map(UsuarioResponse::desde)
                    .stream()
                    .toList();
            return new PaginaResponse<>(pagina, tamano, encontrados.size(), pagina == 1 ? encontrados : List.of());
        }

        PageRequest paginacion = PageRequest.of(pagina - 1, tamano, ORDEN_LISTADO);
        Page<Usuario> usuarios = filtroEstado == null
                ? usuarioRepository.findAll(paginacion)
                : usuarioRepository.findAllByEstado(filtroEstado, paginacion);
        return PaginaResponse.desde(usuarios, UsuarioResponse::desde);
    }

    /** Detalle: también devuelve usuarios INACTIVO (el estado viene en la respuesta). */
    @Transactional(readOnly = true)
    public UsuarioResponse obtener(String cedula) {
        return UsuarioResponse.desde(obtenerExistente(cedula));
    }

    /** Actualiza nombres, apellidos y correo. No cambia la cédula ni el estado. */
    @Transactional
    public UsuarioResponse actualizar(String cedula, ActualizarUsuarioRequest solicitud) {
        Usuario usuario = obtenerExistente(cedula);
        String correo = normalizarCorreo(obligatorio(solicitud.correo(), "correo"));
        validarCorreoLibre(correo, cedula);

        asignarDatos(usuario, solicitud.primerNombre(), solicitud.segundoNombre(),
                solicitud.primerApellido(), solicitud.segundoApellido(), correo);
        return UsuarioResponse.desde(usuarioRepository.saveAndFlush(usuario));
    }

    /** Borrado lógico: pasa a INACTIVO. Si ya lo estaba no hace nada. */
    @Transactional
    public void desactivar(String cedula) {
        obtenerExistente(cedula).setEstado(EstadoUsuario.INACTIVO);
    }

    /** PATCH: cambia a ACTIVO o INACTIVO. Si ya tiene ese estado no cambia nada. Retorna el estado resultante. */
    @Transactional
    public EstadoUsuarioDto cambiarEstado(String cedula, EstadoUsuarioDto solicitud) {
        Usuario usuario = obtenerExistente(cedula);
        EstadoUsuario nuevoEstado = TextoEnum.desdeTexto(EstadoUsuario.class, solicitud.estado(), "estado");
        if (usuario.getEstado() != nuevoEstado) {
            usuario.setEstado(nuevoEstado);
        }
        return new EstadoUsuarioDto(TextoEnum.aTexto(usuario.getEstado()));
    }

    /**
     * Para MovimientoService: el usuario debe existir (404) y estar ACTIVO (409 usuario_inactivo).
     * Se une a la transacción de quien lo llama.
     */
    @Transactional(readOnly = true)
    public Usuario obtenerActivo(String cedula) {
        Usuario usuario = obtenerExistente(cedula);
        if (usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new UsuarioInactivoException();
        }
        return usuario;
    }

    /** El usuario debe existir (404); puede estar INACTIVO. */
    @Transactional(readOnly = true)
    public Usuario obtenerExistente(String cedula) {
        return usuarioRepository.findById(cedula)
                .orElseThrow(() -> RecursoNoEncontradoException.usuario(cedula));
    }

    // ---------------------------------------------------------------------------------

    private void validarCorreoLibre(String correo, String cedulaPropia) {
        boolean ocupado = cedulaPropia == null
                ? usuarioRepository.existsByCorreo(correo)
                : usuarioRepository.existsByCorreoAndCedulaNot(correo, cedulaPropia);
        if (ocupado) {
            throw new ConflictoException("correo_duplicado", "Ya existe un usuario con ese correo",
                    Map.of("correo", correo));
        }
    }

    private static void asignarDatos(Usuario usuario, String primerNombre, String segundoNombre,
                                     String primerApellido, String segundoApellido, String correo) {
        usuario.setPrimerNombre(obligatorio(primerNombre, "primerNombre"));
        usuario.setSegundoNombre(opcional(segundoNombre));
        usuario.setPrimerApellido(obligatorio(primerApellido, "primerApellido"));
        usuario.setSegundoApellido(opcional(segundoApellido));
        usuario.setCorreo(correo);
    }

    /** El DTO ya lo valida con @NotBlank; esto protege al service si se llama desde otro lugar. */
    private static String obligatorio(String valor, String campo) {
        if (vacio(valor)) {
            throw new SolicitudInvalidaException("El campo " + campo + " es obligatorio", campo);
        }
        return valor.trim();
    }

    /** Segundo nombre / segundo apellido: vacío o solo espacios se guarda como null. */
    private static String opcional(String valor) {
        return vacio(valor) ? null : valor.trim();
    }

    private static boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private static String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase(Locale.ROOT);
    }
}
