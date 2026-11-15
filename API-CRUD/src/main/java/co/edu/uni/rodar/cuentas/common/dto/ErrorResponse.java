package co.edu.uni.rodar.cuentas.common.dto;

import java.util.Map;

/** Formato común de error del clúster. */
public record ErrorResponse(String codigo, String mensaje, Map<String, Object> detalle) {
}
