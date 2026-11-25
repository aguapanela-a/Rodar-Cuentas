package co.edu.uni.rodar.cuentas.billeteras.models;

import co.edu.uni.rodar.cuentas.usuarios.models.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "billeteras",
        uniqueConstraints = @UniqueConstraint(name = "uk_billeteras_usuario", columnNames = "usuario_id")
)
@Getter
@Setter
@NoArgsConstructor
public class Billetera {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "billetera_id", nullable = false, updatable = false)
    private UUID billeteraId;

    /** Relación 1 a 1 (usuario_id → usuarios.cedula): la columna es única, así un usuario no puede tener dos billeteras. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario_id",
            referencedColumnName = "cedula",
            nullable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_billeteras_usuario")
    )
    private Usuario usuario;

    /** Saldo en pesos (COP), sin decimales. */
    @Column(name = "saldo", nullable = false)
    private Long saldo = 0L;
}
