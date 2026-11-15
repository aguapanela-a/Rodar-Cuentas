package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Esquema Billetera del contrato. */
public record BilleteraResponse(UUID usuarioId, long saldo, String moneda, OffsetDateTime actualizadoEn) {

    public static final String MONEDA = "COP";
}
