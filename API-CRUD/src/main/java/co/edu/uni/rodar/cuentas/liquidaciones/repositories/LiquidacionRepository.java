package co.edu.uni.rodar.cuentas.liquidaciones.repositories;

import co.edu.uni.rodar.cuentas.liquidaciones.models.Liquidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LiquidacionRepository extends JpaRepository<Liquidacion, UUID> {

    /** Todas las liquidaciones del usuario. */
    List<Liquidacion> findAllByUsuario_Cedula(String cedula);
}
