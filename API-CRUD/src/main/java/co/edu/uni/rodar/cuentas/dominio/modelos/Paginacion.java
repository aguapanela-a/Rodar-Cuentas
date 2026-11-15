package co.edu.uni.rodar.cuentas.dominio.modelos;

import co.edu.uni.rodar.cuentas.dominio.excepciones.DatosInvalidosException;

public record Paginacion(int pagina, int tamano) {

    public static final int PAGINA_POR_DEFECTO = 1;
    public static final int TAMANO_POR_DEFECTO = 20;
    public static final int TAMANO_MAXIMO = 100;

    public Paginacion {
        if (pagina < 1) {
            throw new DatosInvalidosException("pagina", "La página debe ser mayor o igual a 1");
        }
        if (tamano < 1 || tamano > TAMANO_MAXIMO) {
            throw new DatosInvalidosException("tamano", "El tamaño debe estar entre 1 y " + TAMANO_MAXIMO);
        }
    }

    /** Crea la paginación aplicando los valores por defecto (1 y 20) cuando no se envían. */
    public static Paginacion de(Integer pagina, Integer tamano) {
        return new Paginacion(pagina == null ? PAGINA_POR_DEFECTO : pagina,
                tamano == null ? TAMANO_POR_DEFECTO : tamano);
    }

    /** Cantidad de elementos que se saltan antes de la página pedida. */
    public long desplazamiento() {
        return (long) (pagina - 1) * tamano;
    }
}
