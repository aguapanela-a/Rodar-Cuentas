package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.salida.persistencia;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.salida.BilleteraRepositorio;
import co.edu.uni.rodar.cuentas.dominio.modelos.Billetera;

import java.util.Optional;
import java.util.UUID;

/** Pendiente: se implementa con PostgreSQL en la rama de base de datos. No es un bean todavía. */
public class BilleteraPersistenceAdapter implements BilleteraRepositorio {

    @Override
    public Billetera guardar(Billetera billetera) {
        throw new UnsupportedOperationException("Pendiente: rama de base de datos");
    }

    @Override
    public Optional<Billetera> buscarPorUsuarioId(UUID usuarioId) {
        throw new UnsupportedOperationException("Pendiente: rama de base de datos");
    }

    @Override
    public Optional<Billetera> buscarPorUsuarioIdParaActualizar(UUID usuarioId) {
        throw new UnsupportedOperationException("Pendiente: rama de base de datos");
    }
}
