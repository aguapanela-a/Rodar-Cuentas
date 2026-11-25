package co.edu.uni.rodar.cuentas.movimientos.models;

import co.edu.uni.rodar.cuentas.usuarios.models.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "movimientos",
        uniqueConstraints = @UniqueConstraint(name = "uk_movimientos_referencia", columnNames = "referencia")
)
@Getter
@Setter
@NoArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "movimiento_id", nullable = false, updatable = false)
    private UUID movimientoId;

    /** Relación N a 1 (usuario_id → usuarios.cedula): un usuario tiene muchos movimientos, cada movimiento es de un solo usuario. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario_id",
            referencedColumnName = "cedula",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_movimientos_usuario")
    )
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10, updatable = false)
    private TipoMovimiento tipo;

    /** Monto en pesos (COP), sin decimales. */
    @Column(name = "monto", nullable = false, updatable = false)
    private Long monto;

    @Column(name = "referencia", nullable = false, length = 100, updatable = false)
    private String referencia;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
}
