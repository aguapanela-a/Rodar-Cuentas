package co.edu.uni.rodar.cuentas.common.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class Fechas {

    private Fechas() {
    }

    /** El contrato expone las fechas en UTC. */
    public static OffsetDateTime enUtc(OffsetDateTime fecha) {
        return fecha == null ? null : fecha.withOffsetSameInstant(ZoneOffset.UTC);
    }
}
