package co.edu.uni.rodar.cuentas.dominio.modelos;

import co.edu.uni.rodar.cuentas.dominio.excepciones.DatosInvalidosException;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public class Usuario {

    private static final int NOMBRE_MIN = 2;
    private static final int NOMBRE_MAX = 120;
    private static final int CORREO_MAX = 254;
    private static final Pattern FORMATO_CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UUID id;
    private String nombre;
    private String correo;
    private EstadoUsuario estado;
    private final OffsetDateTime creadoEn;

    /** Constructor de reconstrucción: lo usan los adaptadores para rehidratar un usuario ya guardado. */
    public Usuario(UUID id, String nombre, String correo, EstadoUsuario estado, OffsetDateTime creadoEn) {
        this.id = Objects.requireNonNull(id, "id");
        this.nombre = Objects.requireNonNull(nombre, "nombre");
        this.correo = Objects.requireNonNull(correo, "correo");
        this.estado = Objects.requireNonNull(estado, "estado");
        this.creadoEn = Objects.requireNonNull(creadoEn, "creadoEn");
    }

    public static Usuario crear(String nombre, String correo, OffsetDateTime ahora) {
        return new Usuario(UUID.randomUUID(), validarNombre(nombre), normalizarCorreo(correo),
                EstadoUsuario.ACTIVO, Objects.requireNonNull(ahora, "ahora"));
    }

    public void actualizar(String nombre, String correo, EstadoUsuario estado) {
        if (estado == null) {
            throw new DatosInvalidosException("estado", "El estado es obligatorio");
        }
        String nombreValido = validarNombre(nombre);
        String correoValido = normalizarCorreo(correo);
        this.nombre = nombreValido;
        this.correo = correoValido;
        this.estado = estado;
    }

    /** Borrado lógico. */
    public void desactivar() {
        this.estado = EstadoUsuario.INACTIVO;
    }

    public boolean estaActivo() {
        return estado == EstadoUsuario.ACTIVO;
    }

    /** Valida el correo y lo devuelve como se guarda: sin espacios en los extremos y en minúscula. */
    public static String normalizarCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            throw new DatosInvalidosException("correo", "El correo es obligatorio");
        }
        String normalizado = correo.strip().toLowerCase(Locale.ROOT);
        if (normalizado.length() > CORREO_MAX) {
            throw new DatosInvalidosException("correo", "El correo no puede superar " + CORREO_MAX + " caracteres");
        }
        if (!FORMATO_CORREO.matcher(normalizado).matches()) {
            throw new DatosInvalidosException("correo", "El correo no tiene un formato válido");
        }
        return normalizado;
    }

    /** Valida el nombre y lo devuelve sin espacios en los extremos. */
    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DatosInvalidosException("nombre", "El nombre es obligatorio");
        }
        String limpio = nombre.strip();
        if (limpio.length() < NOMBRE_MIN || limpio.length() > NOMBRE_MAX) {
            throw new DatosInvalidosException("nombre",
                    "El nombre debe tener entre " + NOMBRE_MIN + " y " + NOMBRE_MAX + " caracteres");
        }
        return limpio;
    }

    public UUID id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public String correo() {
        return correo;
    }

    public EstadoUsuario estado() {
        return estado;
    }

    public OffsetDateTime creadoEn() {
        return creadoEn;
    }
}
