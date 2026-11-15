package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Esquema MovimientoEntrada del contrato. */
public record RegistrarMovimientoRequest(
        @NotNull(message = "El tipo es obligatorio")
        @Pattern(regexp = "recarga|debito", message = "El tipo debe ser 'recarga' o 'debito'")
        String tipo,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser mayor que cero")
        Long monto,

        @NotBlank(message = "La referencia es obligatoria")
        @Size(max = 100, message = "La referencia no puede superar 100 caracteres")
        String referencia,

        @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
        String descripcion) {
}
