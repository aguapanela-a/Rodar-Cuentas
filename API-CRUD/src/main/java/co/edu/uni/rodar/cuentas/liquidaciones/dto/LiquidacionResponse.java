package co.edu.uni.rodar.cuentas.liquidaciones.dto;

import co.edu.uni.rodar.cuentas.common.util.TextoEnum;
import co.edu.uni.rodar.cuentas.liquidaciones.models.Liquidacion;

import java.util.UUID;

/** Respuesta de POST, GET y PATCH de liquidaciones. */
public record LiquidacionResponse(UUID id, String cedula, String estado) {

    public static LiquidacionResponse desde(Liquidacion liquidacion) {
        return new LiquidacionResponse(
                liquidacion.getLiquidacionId(),
                liquidacion.getUsuario().getCedula(),
                TextoEnum.aTexto(liquidacion.getEstado()));
    }
}
