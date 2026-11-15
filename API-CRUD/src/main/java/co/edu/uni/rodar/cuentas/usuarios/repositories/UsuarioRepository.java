package co.edu.uni.rodar.cuentas.usuarios.repositories;

import co.edu.uni.rodar.cuentas.usuarios.models.EstadoUsuario;
import co.edu.uni.rodar.cuentas.usuarios.models.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    /**
     * Busca un usuario solo si está en el estado indicado.
     * Con ACTIVO sirve para ignorar a los usuarios borrados lógicamente.
     */
    Optional<Usuario> findByUsuarioIdAndEstado(UUID usuarioId, EstadoUsuario estado);

    /** Lista paginada de usuarios por estado (p. ej. solo los ACTIVO). */
    Page<Usuario> findAllByEstado(EstadoUsuario estado, Pageable pageable);

    /**
     * El correo se guarda en minúsculas (ver Usuario#normalizarCorreo),
     * por eso quien llame debe pasar el correo también en minúsculas.
     */
    Optional<Usuario> findByCorreo(String correo);

    /** Para validar el correo único antes de crear un usuario. */
    boolean existsByCorreo(String correo);

    /** Para validar el correo único al actualizar: ignora al propio usuario. */
    boolean existsByCorreoAndUsuarioIdNot(String correo, UUID usuarioId);
}
