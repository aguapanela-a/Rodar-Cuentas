package co.edu.uni.rodar.cuentas.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Cuerpo y respuesta de PATCH /usuarios/{id}: { "estado": "activo" | "inactivo" }. */
public record EstadoUsuarioDto(

        @NotBlank(message = "El estado es obligatorio")
        @Pattern(regexp = "(?i)activo|inactivo", message = "El estado debe ser activo o inactivo")
        String estado
) {
}
