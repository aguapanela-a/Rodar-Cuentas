package co.edu.uni.rodar.cuentas.aplicacion.servicios;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.ConsultarMovimientosUseCase;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.MovimientoRepositorio;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.UsuarioRepositorio;
import co.edu.uni.rodar.cuentas.dominio.excepciones.UsuarioNoEncontradoException;
import co.edu.uni.rodar.cuentas.dominio.modelos.FiltroMovimientos;
import co.edu.uni.rodar.cuentas.dominio.modelos.Movimiento;
import co.edu.uni.rodar.cuentas.dominio.modelos.Pagina;
import co.edu.uni.rodar.cuentas.dominio.modelos.Paginacion;

import java.util.Objects;
import java.util.UUID;

public class ConsultarMovimientosService implements ConsultarMovimientosUseCase {

    private final UsuarioRepositorio usuarios;
    private final MovimientoRepositorio movimientos;

    public ConsultarMovimientosService(UsuarioRepositorio usuarios, MovimientoRepositorio movimientos) {
        this.usuarios = Objects.requireNonNull(usuarios);
        this.movimientos = Objects.requireNonNull(movimientos);
    }

    @Override
    public Pagina<Movimiento> listar(UUID usuarioId, FiltroMovimientos filtro, Paginacion paginacion) {
        if (usuarios.buscarPorId(usuarioId).isEmpty()) {
            throw new UsuarioNoEncontradoException(usuarioId);
        }
        return movimientos.listar(usuarioId,
                filtro == null ? FiltroMovimientos.sinFiltros() : filtro,
                paginacion == null ? Paginacion.de(null, null) : paginacion);
    }
}
