package co.edu.uni.rodar.cuentas.dominio.excepciones;

import java.util.Map;

public class ReferenciaEnConflictoException extends DominioException {

    public ReferenciaEnConflictoException(String referencia) {
        super("referencia_en_conflicto", "La referencia ya se usó con datos distintos",
                Map.of("referencia", referencia));
    }
}
