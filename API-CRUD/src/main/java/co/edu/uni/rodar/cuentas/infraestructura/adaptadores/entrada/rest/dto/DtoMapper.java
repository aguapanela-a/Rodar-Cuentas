package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.ActualizarUsuarioComando;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.CrearUsuarioComando;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.RegistrarMovimientoComando;
import co.edu.uni.rodar.cuentas.dominio.modelos.Billetera;
import co.edu.uni.rodar.cuentas.dominio.modelos.EstadoUsuario;
import co.edu.uni.rodar.cuentas.dominio.modelos.Movimiento;
import co.edu.uni.rodar.cuentas.dominio.modelos.Pagina;
import co.edu.uni.rodar.cuentas.dominio.modelos.TipoMovimiento;
import co.edu.uni.rodar.cuentas.dominio.modelos.Usuario;

import java.util.UUID;
import java.util.function.Function;

/** Traduce entre los DTOs del contrato y los objetos del núcleo. Los enums viajan en minúscula. */
public final class DtoMapper {

    private DtoMapper() {
    }

    public static CrearUsuarioComando aComando(CrearUsuarioRequest request) {
        return new CrearUsuarioComando(request.nombre(), request.correo());
    }

    public static ActualizarUsuarioComando aComando(ActualizarUsuarioRequest request) {
        return new ActualizarUsuarioComando(request.nombre(), request.correo(),
                EstadoUsuario.desde(request.estado()));
    }

    public static RegistrarMovimientoComando aComando(UUID usuarioId, RegistrarMovimientoRequest request) {
        return new RegistrarMovimientoComando(usuarioId, TipoMovimiento.desde(request.tipo()),
                request.monto(), request.referencia(), request.descripcion());
    }

    public static UsuarioResponse aResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.id(), usuario.nombre(), usuario.correo(),
                usuario.estado().valor(), usuario.creadoEn());
    }

    public static BilleteraResponse aResponse(Billetera billetera) {
        return new BilleteraResponse(billetera.usuarioId(), billetera.saldo(), BilleteraResponse.MONEDA,
                billetera.actualizadoEn());
    }

    public static MovimientoResponse aResponse(Movimiento movimiento) {
        return new MovimientoResponse(movimiento.id(), movimiento.usuarioId(), movimiento.tipo().valor(),
                movimiento.monto(), movimiento.referencia(), movimiento.descripcion(),
                movimiento.saldoResultante(), movimiento.creadoEn());
    }

    public static <T, R> PaginaResponse<R> aResponse(Pagina<T> pagina, Function<T, R> transformacion) {
        Pagina<R> transformada = pagina.map(transformacion);
        return new PaginaResponse<>(transformada.datos(), transformada.pagina(), transformada.tamano(),
                transformada.total());
    }
}
