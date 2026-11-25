package co.edu.uni.rodar.cuentas.movimientos.dto;

import co.edu.uni.rodar.cuentas.common.dto.PaginaResponse;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Respuesta de GET /usuarios/{usuarioId}/movimientos:
 * { "cedula": "...", "pagina": 1, "tamano": 20, "total": 3, "datos": [ ... ] }.
 * Los campos de la página se aplanan al mismo nivel que la cédula (como PaginaMovimientos del contrato).
 */
public record HistorialMovimientosResponse(
        String cedula,
        @JsonUnwrapped PaginaResponse<MovimientoHistorialItem> pagina
) {
}
