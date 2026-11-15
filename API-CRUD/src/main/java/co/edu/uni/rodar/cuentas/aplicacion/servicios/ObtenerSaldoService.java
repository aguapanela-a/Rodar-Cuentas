package co.edu.uni.rodar.cuentas.aplicacion.servicios;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.ObtenerSaldoUseCase;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.BilleteraRepositorio;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.UsuarioRepositorio;
import co.edu.uni.rodar.cuentas.dominio.excepciones.UsuarioNoEncontradoException;
import co.edu.uni.rodar.cuentas.dominio.modelos.Billetera;

import java.util.Objects;
import java.util.UUID;

public class ObtenerSaldoService implements ObtenerSaldoUseCase {

    private final UsuarioRepositorio usuarios;
    private final BilleteraRepositorio billeteras;

    public ObtenerSaldoService(UsuarioRepositorio usuarios, BilleteraRepositorio billeteras) {
        this.usuarios = Objects.requireNonNull(usuarios);
        this.billeteras = Objects.requireNonNull(billeteras);
    }

    @Override
    public Billetera obtener(UUID usuarioId) {
        if (usuarios.buscarPorId(usuarioId).isEmpty()) {
            throw new UsuarioNoEncontradoException(usuarioId);
        }
        return billeteras.buscarPorUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalStateException("El usuario " + usuarioId + " no tiene billetera"));
    }
}
