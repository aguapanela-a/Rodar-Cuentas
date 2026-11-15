package co.edu.uni.rodar.cuentas.dominio.modelos;

import java.util.Locale;

/** Filtros opcionales (null = sin filtro). El correo se compara exacto, sin importar mayúsculas. */
public record FiltroUsuarios(EstadoUsuario estado, String correo) {

    public FiltroUsuarios {
        correo = (correo == null || correo.isBlank()) ? null : correo.strip().toLowerCase(Locale.ROOT);
    }

    public static FiltroUsuarios sinFiltros() {
        return new FiltroUsuarios(null, null);
    }

    public boolean cumple(Usuario usuario) {
        return (estado == null || usuario.estado() == estado)
                && (correo == null || usuario.correo().equals(correo));
    }
}
