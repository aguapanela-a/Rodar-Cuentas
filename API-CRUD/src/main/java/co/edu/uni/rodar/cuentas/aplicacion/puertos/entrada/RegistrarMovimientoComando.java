package co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada;

import co.edu.uni.rodar.cuentas.dominio.modelos.TipoMovimiento;

import java.util.UUID;

public record RegistrarMovimientoComando(UUID usuarioId, TipoMovimiento tipo, long monto,
                                         String referencia, String descripcion) {
}
