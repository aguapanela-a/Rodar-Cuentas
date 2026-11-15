package co.edu.uni.rodar.cuentas.dominio;

import co.edu.uni.rodar.cuentas.dominio.excepciones.DatosInvalidosException;
import co.edu.uni.rodar.cuentas.dominio.modelos.EstadoUsuario;
import co.edu.uni.rodar.cuentas.dominio.modelos.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UsuarioTest {

    private static final OffsetDateTime AHORA = OffsetDateTime.of(2026, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "ana", "ana@", "@example.com", "ana@example", "ana gomez@example.com"})
    void correoInvalidoEsRechazado(String correo) {
        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class,
                () -> Usuario.crear("Ana Gómez", correo, AHORA));

        assertEquals("correo", ex.detalle().get("campo"));
    }

    @Test
    void correoDeMasDe254CaracteresEsRechazado() {
        String correo = "a".repeat(250) + "@x.co";

        assertThrows(DatosInvalidosException.class, () -> Usuario.crear("Ana Gómez", correo, AHORA));
    }

    @Test
    void crearNormalizaCorreoYNombreYQuedaActivo() {
        Usuario usuario = Usuario.crear("  Ana Gómez ", "  Ana@Example.COM ", AHORA);

        assertEquals("Ana Gómez", usuario.nombre());
        assertEquals("ana@example.com", usuario.correo());
        assertEquals(EstadoUsuario.ACTIVO, usuario.estado());
        assertEquals(AHORA, usuario.creadoEn());
    }

    @Test
    void nombreDeUnSoloCaracterEsRechazado() {
        DatosInvalidosException ex = assertThrows(DatosInvalidosException.class,
                () -> Usuario.crear(" A ", "ana@example.com", AHORA));

        assertEquals("nombre", ex.detalle().get("campo"));
    }
}
