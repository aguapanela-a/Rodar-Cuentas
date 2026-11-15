package co.edu.uni.rodar.cuentas.common.util;

import co.edu.uni.rodar.cuentas.common.exceptions.SolicitudInvalidaException;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Los enums de los modelos están en mayúsculas (ACTIVO, RECARGA), pero el contrato los
 * expone en minúsculas (activo, recarga). Esta clase convierte en ambos sentidos.
 */
public final class TextoEnum {

    private TextoEnum() {
    }

    public static String aTexto(Enum<?> valor) {
        return valor.name().toLowerCase(Locale.ROOT);
    }

    /** Acepta mayúsculas o minúsculas. Valor desconocido → 400 solicitud_invalida. */
    public static <E extends Enum<E>> E desdeTexto(Class<E> tipo, String texto, String campo) {
        try {
            return Enum.valueOf(tipo, texto.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            String permitidos = Arrays.stream(tipo.getEnumConstants())
                    .map(TextoEnum::aTexto)
                    .collect(Collectors.joining(", "));
            throw new SolicitudInvalidaException(
                    "Valor inválido para " + campo + ". Permitidos: " + permitidos, campo);
        }
    }
}
