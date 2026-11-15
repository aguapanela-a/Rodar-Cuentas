package co.edu.uni.rodar.cuentas.aplicacion.puertos.salida;

import java.util.function.Supplier;

/**
 * Ejecuta una operación de forma atómica: o se aplican todos sus cambios o ninguno.
 * En base de datos será una transacción; en memoria, un candado.
 */
public interface UnidadDeTrabajo {

    <T> T ejecutar(Supplier<T> operacion);
}
