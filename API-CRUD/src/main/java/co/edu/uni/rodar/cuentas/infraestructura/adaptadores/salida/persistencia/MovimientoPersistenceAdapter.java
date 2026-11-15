package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.salida.persistencia;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.MovimientoRepositorio;
import co.edu.uni.rodar.cuentas.dominio.modelos.FiltroMovimientos;
import co.edu.uni.rodar.cuentas.dominio.modelos.Movimiento;
import co.edu.uni.rodar.cuentas.dominio.modelos.Pagina;
import co.edu.uni.rodar.cuentas.dominio.modelos.Paginacion;

import java.util.Optional;
import java.util.UUID;

/** Pendiente: se implementa con PostgreSQL en la rama de base de datos. No es un bean todavía. */
public class MovimientoPersistenceAdapter implements MovimientoRepositorio {

    @Override
    public Movimiento guardar(Movimiento movimiento) {
        throw new UnsupportedOperationException("Pendiente: rama de base de datos");
    }

    @Override
    public Optional<Movimiento> buscarPorReferencia(String referencia) {
        throw new UnsupportedOperationException("Pendiente: rama de base de datos");
    }

    @Override
    public Pagina<Movimiento> listar(UUID usuarioId, FiltroMovimientos filtro, Paginacion paginacion) {
        throw new UnsupportedOperationException("Pendiente: rama de base de datos");
    }
}
