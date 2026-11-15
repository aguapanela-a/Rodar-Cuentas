package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Esquema Movimiento del contrato. La descripción es opcional: si no hay, no se envía. */
public record MovimientoResponse(
        UUID id,
        UUID usuarioId,
        String tipo,
        long monto,
        String referencia,
        @JsonInclude(JsonInclude.Include.NON_NULL) String descripcion,
        long saldoResultante,
        OffsetDateTime creadoEn) {
}
