package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto;

import java.util.List;

/** Esquemas PaginaUsuarios / PaginaMovimientos del contrato (MetaPaginacion + datos). */
public record PaginaResponse<T>(List<T> datos, int pagina, int tamano, long total) {
}
