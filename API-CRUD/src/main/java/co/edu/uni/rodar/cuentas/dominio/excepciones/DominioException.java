package co.edu.uni.rodar.cuentas.dominio.excepciones;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base de las excepciones de negocio. Lleva un código estable (snake_case) y un detalle
 * que la infraestructura traduce al formato de error del clúster {codigo, mensaje, detalle}.
 */
public abstract class DominioException extends RuntimeException {

    private final String codigo;
    private final Map<String, Object> detalle;

    protected DominioException(String codigo, String mensaje, Map<String, Object> detalle) {
        super(mensaje);
        this.codigo = codigo;
        this.detalle = Collections.unmodifiableMap(new LinkedHashMap<>(detalle));
    }

    public String codigo() {
        return codigo;
    }

    public Map<String, Object> detalle() {
        return detalle;
    }
}
