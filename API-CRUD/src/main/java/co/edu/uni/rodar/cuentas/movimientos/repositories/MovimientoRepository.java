package co.edu.uni.rodar.cuentas.movimientos.repositories;

import co.edu.uni.rodar.cuentas.movimientos.models.Movimiento;
import co.edu.uni.rodar.cuentas.movimientos.models.TipoMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, UUID> {

    /** Historial de un usuario, del más reciente al más antiguo. */
    Page<Movimiento> findByUsuario_CedulaOrderByCreadoEnDesc(String cedula, Pageable pageable);

    /** Historial de un usuario filtrado por tipo (solo recargas o solo débitos). */
    Page<Movimiento> findByUsuario_CedulaAndTipoOrderByCreadoEnDesc(String cedula, TipoMovimiento tipo,
                                                                  Pageable pageable);

    /** La referencia es única: permite detectar un movimiento repetido y devolver el ya registrado. */
    Optional<Movimiento> findByReferencia(String referencia);

    boolean existsByReferencia(String referencia);
}
