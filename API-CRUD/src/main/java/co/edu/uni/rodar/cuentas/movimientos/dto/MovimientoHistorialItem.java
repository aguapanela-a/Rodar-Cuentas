package co.edu.uni.rodar.cuentas.movimientos.dto;

import co.edu.uni.rodar.cuentas.common.util.Fechas;
import co.edu.uni.rodar.cuentas.common.util.TextoEnum;
import co.edu.uni.rodar.cuentas.movimientos.models.Movimiento;

import java.time.OffsetDateTime;

/** Un elemento del historial. La cédula no se repite aquí: va una sola vez en HistorialMovimientosResponse. */
public record MovimientoHistorialItem(long monto, String tipo, String referencia, OffsetDateTime creadoEn) {

    public static MovimientoHistorialItem desde(Movimiento movimiento) {
        return new MovimientoHistorialItem(
                movimiento.getMonto(),
                TextoEnum.aTexto(movimiento.getTipo()),
                movimiento.getReferencia(),
                Fechas.enUtc(movimiento.getCreadoEn()));
    }
}
