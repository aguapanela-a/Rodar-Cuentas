package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.salida.memoria;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.UnidadDeTrabajo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * TEMPORAL (perfil "memoria"). Simula la atomicidad serializando las operaciones con un candado:
 * dos operaciones nunca se intercalan. No hay rollback; por eso los servicios validan todo antes
 * de llamar a guardar(). En la rama de base de datos se reemplaza por una transacción.
 */
@Component
@Profile("memoria")
public class UnidadDeTrabajoMemoria implements UnidadDeTrabajo {

    private final ReentrantLock candado = new ReentrantLock();

    @Override
    public <T> T ejecutar(Supplier<T> operacion) {
        candado.lock();
        try {
            return operacion.get();
        } finally {
            candado.unlock();
        }
    }
}
