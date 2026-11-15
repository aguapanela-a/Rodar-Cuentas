package co.edu.uni.rodar.cuentas.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/** Página del contrato (MetaPaginacion + datos). La página empieza en 1. */
public record PaginaResponse<T>(int pagina, int tamano, long total, List<T> datos) {

    public static <E, T> PaginaResponse<T> desde(Page<E> pagina, Function<E, T> convertir) {
        return new PaginaResponse<>(
                pagina.getNumber() + 1,
                pagina.getSize(),
                pagina.getTotalElements(),
                pagina.getContent().stream().map(convertir).toList());
    }
}
