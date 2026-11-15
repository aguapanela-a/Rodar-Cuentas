package co.edu.uni.rodar.cuentas.aplicacion.puertos.salida;

import co.edu.uni.rodar.cuentas.dominio.modelos.FiltroMovimientos;
import co.edu.uni.rodar.cuentas.dominio.modelos.Movimiento;
import co.edu.uni.rodar.cuentas.dominio.modelos.Pagina;
import co.edu.uni.rodar.cuentas.dominio.modelos.Paginacion;

import java.util.Optional;
import java.util.UUID;

public interface MovimientoRepositorio {

    Movimiento guardar(Movimiento movimiento);

    Optional<Movimiento> buscarPorReferencia(String referencia);

    /** Movimientos del usuario, del más reciente al más antiguo. */
    Pagina<Movimiento> listar(UUID usuarioId, FiltroMovimientos filtro, Paginacion paginacion);
}
