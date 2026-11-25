package co.edu.uni.rodar.cuentas.common.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Base de las excepciones propias. Cada una sabe su código HTTP y su código estable
 * (snake_case) del formato de error del contrato: { "codigo", "mensaje", "detalle" }.
 */
public abstract class ApiException extends RuntimeException {

    private final HttpStatus estadoHttp;
    private final String codigo;
    private final Map<String, Object> detalle;

    protected ApiException(HttpStatus estadoHttp, String codigo, String mensaje, Map<String, Object> detalle) {
        super(mensaje);
        this.estadoHttp = estadoHttp;
        this.codigo = codigo;
        this.detalle = detalle == null ? Map.of() : Map.copyOf(detalle);
    }

    public HttpStatus getEstadoHttp() {
        return estadoHttp;
    }

    public String getCodigo() {
        return codigo;
    }

    public Map<String, Object> getDetalle() {
        return detalle;
    }
}
