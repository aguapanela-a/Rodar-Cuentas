package co.edu.uni.rodar.cuentas.aplicacion.puertos.salida;

import co.edu.uni.rodar.cuentas.dominio.modelos.Billetera;

import java.util.Optional;
import java.util.UUID;

public interface BilleteraRepositorio {

    Billetera guardar(Billetera billetera);

    Optional<Billetera> buscarPorUsuarioId(UUID usuarioId);

    /**
     * Igual que {@link #buscarPorUsuarioId(UUID)}, pero bloquea la billetera hasta que termine
     * la unidad de trabajo (en base de datos: SELECT ... FOR UPDATE).
     */
    Optional<Billetera> buscarPorUsuarioIdParaActualizar(UUID usuarioId);
}
