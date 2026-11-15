package co.edu.uni.rodar.cuentas.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Cuerpo de PUT /usuarios/{id}. La cédula viene en la ruta y no se puede cambiar; el estado se cambia con PATCH. */
public record ActualizarUsuarioRequest(

        @NotBlank(message = "El primer nombre es obligatorio")
        @Size(max = 50, message = "El primer nombre admite máximo 50 caracteres")
        String primerNombre,

        @Size(max = 50, message = "El segundo nombre admite máximo 50 caracteres")
        String segundoNombre,

        @NotBlank(message = "El primer apellido es obligatorio")
        @Size(max = 50, message = "El primer apellido admite máximo 50 caracteres")
        String primerApellido,

        @Size(max = 50, message = "El segundo apellido admite máximo 50 caracteres")
        String segundoApellido,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 254, message = "El correo admite máximo 254 caracteres")
        String correo
) {
}
