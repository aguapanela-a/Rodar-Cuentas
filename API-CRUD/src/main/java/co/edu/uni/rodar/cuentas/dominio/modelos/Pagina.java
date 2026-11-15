package co.edu.uni.rodar.cuentas.dominio.modelos;

import java.util.List;
import java.util.function.Function;

public record Pagina<T>(List<T> datos, int pagina, int tamano, long total) {

    public Pagina {
        datos = List.copyOf(datos);
    }

    public <R> Pagina<R> map(Function<T, R> transformacion) {
        return new Pagina<>(datos.stream().map(transformacion).toList(), pagina, tamano, total);
    }
}
