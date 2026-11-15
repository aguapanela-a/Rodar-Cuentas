package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Esquema UsuarioEntrada del contrato. */
public record CrearUsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 120, message = "El nombre debe tener entre 2 y 120 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 254, message = "El correo no puede superar 254 caracteres")
        String correo) {
}
