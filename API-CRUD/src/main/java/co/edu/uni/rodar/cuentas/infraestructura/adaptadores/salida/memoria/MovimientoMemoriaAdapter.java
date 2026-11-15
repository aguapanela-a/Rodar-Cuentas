package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.salida.memoria;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.MovimientoRepositorio;
import co.edu.uni.rodar.cuentas.dominio.modelos.FiltroMovimientos;
import co.edu.uni.rodar.cuentas.dominio.modelos.Movimiento;
import co.edu.uni.rodar.cuentas.dominio.modelos.Pagina;
import co.edu.uni.rodar.cuentas.dominio.modelos.Paginacion;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * TEMPORAL (perfil "memoria"). Indexa por referencia, que es única (clave de idempotencia).
 * Movimiento es inmutable, así que no hace falta copiarlo.
 */
@Component
@Profile("memoria")
public class MovimientoMemoriaAdapter implements MovimientoRepositorio {

    /** La secuencia desempata movimientos creados en el mismo instante (orden de inserción). */
    private record Registro(Movimiento movimiento, long secuencia) {
    }

    private final Map<String, Registro> porReferencia = new ConcurrentHashMap<>();
    private final AtomicLong secuencia = new AtomicLong();

    @Override
    public Movimiento guardar(Movimiento movimiento) {
        Registro nuevo = new Registro(movimiento, secuencia.incrementAndGet());
        if (porReferencia.putIfAbsent(movimiento.referencia(), nuevo) != null) {
            throw new IllegalStateException("Ya existe un movimiento con la referencia " + movimiento.referencia());
        }
        return movimiento;
    }

    @Override
    public Optional<Movimiento> buscarPorReferencia(String referencia) {
        if (referencia == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(porReferencia.get(referencia)).map(Registro::movimiento);
    }

    @Override
    public Pagina<Movimiento> listar(UUID usuarioId, FiltroMovimientos filtro, Paginacion paginacion) {
        Comparator<Registro> masRecientePrimero = Comparator
                .comparing((Registro r) -> r.movimiento().creadoEn())
                .thenComparingLong(Registro::secuencia)
                .reversed();
        List<Movimiento> filtrados = porReferencia.values().stream()
                .filter(r -> r.movimiento().usuarioId().equals(usuarioId))
                .filter(r -> filtro.cumple(r.movimiento()))
                .sorted(masRecientePrimero)
                .map(Registro::movimiento)
                .toList();
        List<Movimiento> datos = filtrados.stream()
                .skip(paginacion.desplazamiento())
                .limit(paginacion.tamano())
                .toList();
        return new Pagina<>(datos, paginacion.pagina(), paginacion.tamano(), filtrados.size());
    }
}
