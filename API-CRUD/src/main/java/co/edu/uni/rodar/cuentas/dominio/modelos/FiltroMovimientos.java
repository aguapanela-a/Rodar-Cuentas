package co.edu.uni.rodar.cuentas.dominio.modelos;

import co.edu.uni.rodar.cuentas.dominio.excepciones.DatosInvalidosException;

import java.time.OffsetDateTime;

/**
 * Filtros opcionales (null = sin filtro). Según el contrato, {@code desde} es inclusivo
 * y {@code hasta} es exclusivo.
 */
public record FiltroMovimientos(TipoMovimiento tipo, OffsetDateTime desde, OffsetDateTime hasta) {

    public FiltroMovimientos {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatosInvalidosException("desde", "'desde' debe ser anterior o igual a 'hasta'");
        }
    }

    public static FiltroMovimientos sinFiltros() {
        return new FiltroMovimientos(null, null, null);
    }

    public boolean cumple(Movimiento movimiento) {
        return (tipo == null || movimiento.tipo() == tipo)
                && (desde == null || !movimiento.creadoEn().isBefore(desde))
                && (hasta == null || movimiento.creadoEn().isBefore(hasta));
    }
}
