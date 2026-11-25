package co.edu.uni.rodar.cuentas.usuarios.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.util.Locale;

@Entity
@Table(
        name = "usuarios",
        uniqueConstraints = @UniqueConstraint(name = "uk_usuarios_correo", columnNames = "correo")
)
@Getter
@Setter
@NoArgsConstructor
public class Usuario implements Persistable<String> {

    /** Cédula del usuario: la elige el usuario (no se autogenera) y es la clave primaria. */
    @Id
    @Column(name = "cedula", nullable = false, updatable = false, length = 20)
    private String cedula;

    @Column(name = "primer_nombre", nullable = false, length = 50)
    private String primerNombre;

    @Column(name = "segundo_nombre", length = 50)
    private String segundoNombre;

    @Column(name = "primer_apellido", nullable = false, length = 50)
    private String primerApellido;

    @Column(name = "segundo_apellido", length = 50)
    private String segundoApellido;

    @Column(name = "correo", nullable = false, length = 254)
    private String correo;

    /** Borrado lógico: un usuario eliminado pasa a INACTIVO, la fila no se borra. */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 10)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    /**
     * Como la cédula no es autogenerada, Spring Data no puede saber si el usuario es nuevo
     * mirando si el id es null. Sin esta bandera, save() de un usuario nuevo con una cédula
     * que ya existe sobrescribiría al usuario existente en vez de fallar por clave duplicada.
     */
    @Transient
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private boolean nuevo = true;

    @Override
    public String getId() {
        return cedula;
    }

    @Override
    public boolean isNew() {
        return nuevo;
    }

    /** Un usuario leído de la BD o ya guardado deja de ser nuevo: el próximo save() es un UPDATE. */
    @PostLoad
    @PostPersist
    private void marcarComoExistente() {
        nuevo = false;
    }

    /**
     * El correo se guarda siempre en minúsculas y sin espacios, así la restricción única
     * también impide duplicados como "Ana@Correo.com" y "ana@correo.com".
     */
    @PrePersist
    @PreUpdate
    private void normalizarCorreo() {
        if (correo != null) {
            correo = correo.trim().toLowerCase(Locale.ROOT);
        }
    }
}
