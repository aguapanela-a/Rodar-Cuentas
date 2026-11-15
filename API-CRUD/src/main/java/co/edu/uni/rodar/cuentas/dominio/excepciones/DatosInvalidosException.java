package co.edu.uni.rodar.cuentas.dominio.excepciones;

import java.util.Map;

public class DatosInvalidosException extends DominioException {

    public DatosInvalidosException(String campo, String mensaje) {
        super("solicitud_invalida", mensaje, Map.of("campo", campo));
    }
}
