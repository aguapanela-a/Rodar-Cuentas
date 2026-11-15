package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Esquema Usuario del contrato. */
public record UsuarioResponse(UUID id, String nombre, String correo, String estado, OffsetDateTime creadoEn) {
}
