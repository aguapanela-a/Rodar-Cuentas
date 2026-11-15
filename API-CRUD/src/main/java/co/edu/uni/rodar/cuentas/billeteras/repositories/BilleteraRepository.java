package co.edu.uni.rodar.cuentas.billeteras.repositories;

import co.edu.uni.rodar.cuentas.billeteras.models.Billetera;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BilleteraRepository extends JpaRepository<Billetera, UUID> {

    /** Billetera del usuario (relación 1 a 1), solo para lectura. */
    Optional<Billetera> findByUsuario_UsuarioId(UUID usuarioId);

    /** Para no crear una segunda billetera al mismo usuario. */
    boolean existsByUsuario_UsuarioId(UUID usuarioId);

    /**
     * Billetera del usuario bloqueada para modificar el saldo.
     * En PostgreSQL se traduce a SELECT ... FOR UPDATE: si dos recargas o débitos llegan a la vez,
     * el segundo espera a que termine el primero y lee el saldo ya actualizado.
     * Debe llamarse dentro de una transacción (@Transactional en el servicio).
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Billetera b where b.usuario.usuarioId = :usuarioId")
    Optional<Billetera> findByUsuarioIdForUpdate(@Param("usuarioId") UUID usuarioId);
}
