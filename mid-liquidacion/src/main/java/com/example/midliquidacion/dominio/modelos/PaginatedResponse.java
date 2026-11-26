package com.example.midliquidacion.dominio.modelos;

import java.util.List;

public record PaginatedResponse<T>(
    List<T> content,
    Integer totalElements,
    Integer totalPages
) {}