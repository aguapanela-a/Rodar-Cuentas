package co.edu.uni.rodar.cuentas.liquidaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Cuerpo de PATCH /liquidaciones/{liquidacionId}: { "estado": "pendiente" | "realizada" }. */
public record EstadoLiquidacionRequest(

        @NotBlank(message = "El estado es obligatorio")
        @Pattern(regexp = "(?i)pendiente|realizada", message = "El estado debe ser pendiente o realizada")
        String estado
) {
}
