package co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada;

import co.edu.uni.rodar.cuentas.dominio.modelos.FiltroUsuarios;
import co.edu.uni.rodar.cuentas.dominio.modelos.Pagina;
import co.edu.uni.rodar.cuentas.dominio.modelos.Paginacion;
import co.edu.uni.rodar.cuentas.dominio.modelos.Usuario;

import java.util.UUID;

public interface GestionarUsuarioUseCase {

    /** Crea el usuario y su billetera con saldo 0. */
    Usuario crear(CrearUsuarioComando comando);

    Usuario obtener(UUID id);

    Pagina<Usuario> listar(FiltroUsuarios filtro, Paginacion paginacion);

    Usuario actualizar(UUID id, ActualizarUsuarioComando comando);

    /** Borrado lógico: el usuario queda en estado inactivo. */
    void desactivar(UUID id);
}
