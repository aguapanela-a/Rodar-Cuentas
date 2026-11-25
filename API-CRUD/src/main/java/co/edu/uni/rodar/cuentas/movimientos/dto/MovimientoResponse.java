package co.edu.uni.rodar.cuentas.movimientos.dto;

import co.edu.uni.rodar.cuentas.common.util.Fechas;
import co.edu.uni.rodar.cuentas.common.util.TextoEnum;
import co.edu.uni.rodar.cuentas.movimientos.models.Movimiento;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Respuesta de POST /usuarios/{usuarioId}/movimientos (201 nuevo, 200 idempotente). */
public record MovimientoResponse(UUID id, String cedula, String tipo, long monto, String referencia,
                                 OffsetDateTime creadoEn) {

    public static MovimientoResponse desde(Movimiento movimiento) {
        return new MovimientoResponse(
                movimiento.getMovimientoId(),
                movimiento.getUsuario().getCedula(),
                TextoEnum.aTexto(movimiento.getTipo()),
                movimiento.getMonto(),
                movimiento.getReferencia(),
                Fechas.enUtc(movimiento.getCreadoEn()));
    }
}
