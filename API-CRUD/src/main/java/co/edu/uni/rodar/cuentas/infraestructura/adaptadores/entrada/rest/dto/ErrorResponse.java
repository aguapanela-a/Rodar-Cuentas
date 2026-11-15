package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto;

import java.util.Map;

/** Formato común de error del clúster: {codigo, mensaje, detalle}. detalle nunca es null. */
public record ErrorResponse(String codigo, String mensaje, Map<String, Object> detalle) {

    public ErrorResponse {
        detalle = detalle == null ? Map.of() : detalle;
    }

    public ErrorResponse(String codigo, String mensaje) {
        this(codigo, mensaje, Map.of());
    }
}
