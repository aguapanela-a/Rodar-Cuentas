package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.salida.memoria;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.BilleteraRepositorio;
import co.edu.uni.rodar.cuentas.dominio.modelos.Billetera;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** TEMPORAL (perfil "memoria"). Guarda copias de las billeteras, igual que lo haría una base de datos. */
@Component
@Profile("memoria")
public class BilleteraMemoriaAdapter implements BilleteraRepositorio {

    private final Map<UUID, Billetera> billeteras = new ConcurrentHashMap<>();

    @Override
    public Billetera guardar(Billetera billetera) {
        billeteras.put(billetera.usuarioId(), copiar(billetera));
        return copiar(billetera);
    }

    @Override
    public Optional<Billetera> buscarPorUsuarioId(UUID usuarioId) {
        return Optional.ofNullable(billeteras.get(usuarioId)).map(BilleteraMemoriaAdapter::copiar);
    }

    /**
     * En memoria no hay bloqueo por fila: la exclusión mutua la da UnidadDeTrabajoMemoria,
     * que serializa todas las operaciones de escritura.
     */
    @Override
    public Optional<Billetera> buscarPorUsuarioIdParaActualizar(UUID usuarioId) {
        return buscarPorUsuarioId(usuarioId);
    }

    private static Billetera copiar(Billetera b) {
        return new Billetera(b.usuarioId(), b.saldo(), b.actualizadoEn());
    }
}
