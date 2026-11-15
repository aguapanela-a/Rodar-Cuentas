package co.edu.uni.rodar.cuentas.movimientos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo de POST /usuarios/{usuarioId}/movimientos.
 * <p>
 * monto solo se exige presente aquí: que sea mayor que cero (C-HU1) lo valida el service,
 * después de comprobar que el usuario existe y está activo (orden pedido).
 * <p>
 * descripcion existe en el contrato (el MID de liquidación la envía), pero el modelo no tiene
 * esa columna: se acepta para no rechazar peticiones válidas según el contrato y no se guarda.
 */
public record MovimientoRequest(

        @NotBlank(message = "El tipo es obligatorio")
        @Pattern(regexp = "(?i)recarga|debito", message = "El tipo debe ser recarga o debito")
        String tipo,

        @NotNull(message = "El monto es obligatorio")
        Long monto,

        @NotBlank(message = "La referencia es obligatoria")
        @Size(max = 100, message = "La referencia admite máximo 100 caracteres")
        String referencia,

        @Size(max = 255, message = "La descripción admite máximo 255 caracteres")
        String descripcion
) {
}
