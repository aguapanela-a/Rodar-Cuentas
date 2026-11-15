package co.edu.uni.rodar.cuentas.movimientos.services;

import co.edu.uni.rodar.cuentas.movimientos.dto.MovimientoResponse;

/**
 * Resultado interno de registrar un movimiento. {@code creado} indica si se registró uno nuevo (201)
 * o si se devolvió el original por idempotencia (200). No se serializa.
 */
public record ResultadoMovimiento(MovimientoResponse movimiento, boolean creado) {
}
