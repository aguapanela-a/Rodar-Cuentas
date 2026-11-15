package co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada;

import co.edu.uni.rodar.cuentas.dominio.modelos.Movimiento;

/**
 * @param yaExistia true si la referencia ya estaba registrada con los mismos datos y se devolvió
 *                  el movimiento original (en REST: 200 en lugar de 201).
 */
public record ResultadoMovimiento(Movimiento movimiento, boolean yaExistia) {
}
