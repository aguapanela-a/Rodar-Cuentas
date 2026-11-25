package co.edu.uni.rodar.cuentas.usuarios.dto;

import co.edu.uni.rodar.cuentas.common.util.TextoEnum;
import co.edu.uni.rodar.cuentas.usuarios.models.Usuario;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Respuesta de GET, POST y PUT de usuarios. */
public record UsuarioResponse(String cedula, String nombreCompleto, String correo, String estado) {

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getCedula(),
                nombreCompleto(usuario),
                usuario.getCorreo(),
                TextoEnum.aTexto(usuario.getEstado()));
    }

    /** Une los nombres y apellidos presentes con un solo espacio: "Nicolas Castro" si faltan los segundos. */
    private static String nombreCompleto(Usuario usuario) {
        return Stream.of(usuario.getPrimerNombre(), usuario.getSegundoNombre(),
                        usuario.getPrimerApellido(), usuario.getSegundoApellido())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(parte -> !parte.isEmpty())
                .collect(Collectors.joining(" "));
    }
}
