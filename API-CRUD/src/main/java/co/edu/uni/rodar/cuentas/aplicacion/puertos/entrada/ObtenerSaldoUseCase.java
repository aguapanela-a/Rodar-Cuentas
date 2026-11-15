package co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada;

import co.edu.uni.rodar.cuentas.dominio.modelos.Billetera;

import java.util.UUID;

public interface ObtenerSaldoUseCase {

    Billetera obtener(UUID usuarioId);
}
