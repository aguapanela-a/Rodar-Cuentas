package co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada;

public interface RegistrarMovimientoUseCase {

    /** Registra una recarga o un débito. Es idempotente por referencia. */
    ResultadoMovimiento registrar(RegistrarMovimientoComando comando);
}
