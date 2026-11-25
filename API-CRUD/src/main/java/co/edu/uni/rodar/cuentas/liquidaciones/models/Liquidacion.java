package co.edu.uni.rodar.cuentas.liquidaciones.models;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "liquidaciones")
@Getter
@Setter
@NoArgsConstructor
public class Liquidacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "liquidacion_id", nullable = false, updatable = false)
    private UUID liquidacionId;

    /** Relación N a 1 (usuario_id → usuarios.cedula): un usuario puede tener muchas liquidaciones. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario_id",
            referencedColumnName = "cedula",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_liquidaciones_usuario")
    )
    private Usuario usuario;

    /** Toda liquidación nace PENDIENTE. */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 10)
    private EstadoLiquidacion estado = EstadoLiquidacion.PENDIENTE;
}
