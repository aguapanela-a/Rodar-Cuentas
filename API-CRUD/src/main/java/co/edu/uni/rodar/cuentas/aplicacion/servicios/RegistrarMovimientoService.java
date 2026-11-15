package co.edu.uni.rodar.cuentas.aplicacion.servicios;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.RegistrarMovimientoComando;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.RegistrarMovimientoUseCase;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.ResultadoMovimiento;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.BilleteraRepositorio;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.MovimientoRepositorio;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.UnidadDeTrabajo;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.UsuarioRepositorio;
import co.edu.uni.rodar.cuentas.dominio.excepciones.DatosInvalidosException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.ReferenciaEnConflictoException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.UsuarioInactivoException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.UsuarioNoEncontradoException;
import co.edu.uni.rodar.cuentas.dominio.modelos.Billetera;
import co.edu.uni.rodar.cuentas.dominio.modelos.Movimiento;
import co.edu.uni.rodar.cuentas.dominio.modelos.Usuario;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

public class RegistrarMovimientoService implements RegistrarMovimientoUseCase {

    private final UsuarioRepositorio usuarios;
    private final BilleteraRepositorio billeteras;
    private final MovimientoRepositorio movimientos;
    private final UnidadDeTrabajo unidadDeTrabajo;
    private final Clock reloj;

    public RegistrarMovimientoService(UsuarioRepositorio usuarios, BilleteraRepositorio billeteras,
                                      MovimientoRepositorio movimientos, UnidadDeTrabajo unidadDeTrabajo,
                                      Clock reloj) {
        this.usuarios = Objects.requireNonNull(usuarios);
        this.billeteras = Objects.requireNonNull(billeteras);
        this.movimientos = Objects.requireNonNull(movimientos);
        this.unidadDeTrabajo = Objects.requireNonNull(unidadDeTrabajo);
        this.reloj = Objects.requireNonNull(reloj);
    }

    @Override
    public ResultadoMovimiento registrar(RegistrarMovimientoComando comando) {
        if (comando.tipo() == null) {
            throw new DatosInvalidosException("tipo", "El tipo es obligatorio");
        }
        Movimiento.validarReferencia(comando.referencia());

        return unidadDeTrabajo.ejecutar(() -> {
            // 1. Idempotencia por referencia.
            Optional<Movimiento> existente = movimientos.buscarPorReferencia(comando.referencia());
            if (existente.isPresent()) {
                Movimiento original = existente.get();
                if (original.mismosDatos(comando.usuarioId(), comando.tipo(), comando.monto())) {
                    return new ResultadoMovimiento(original, true);
                }
                throw new ReferenciaEnConflictoException(comando.referencia());
            }

            // 2. El usuario debe existir y estar activo.
            Usuario usuario = usuarios.buscarPorId(comando.usuarioId())
                    .orElseThrow(() -> new UsuarioNoEncontradoException(comando.usuarioId()));
            if (!usuario.estaActivo()) {
                throw new UsuarioInactivoException();
            }

            // 3. Billetera bloqueada mientras dure la operación; aplicar el movimiento.
            Billetera billetera = billeteras.buscarPorUsuarioIdParaActualizar(usuario.id())
                    .orElseThrow(() -> new IllegalStateException("El usuario " + usuario.id() + " no tiene billetera"));
            OffsetDateTime ahora = OffsetDateTime.now(reloj);
            switch (comando.tipo()) {
                case RECARGA -> billetera.recargar(comando.monto(), ahora);
                case DEBITO -> billetera.debitar(comando.monto(), ahora);
            }

            // 4. Se valida el movimiento antes de guardar nada, para no persistir un saldo sin su movimiento.
            Movimiento movimiento = Movimiento.registrar(usuario.id(), comando.tipo(), comando.monto(),
                    comando.referencia(), comando.descripcion(), billetera.saldo(), ahora);
            billeteras.guardar(billetera);
            return new ResultadoMovimiento(movimientos.guardar(movimiento), false);
        });
    }
}
