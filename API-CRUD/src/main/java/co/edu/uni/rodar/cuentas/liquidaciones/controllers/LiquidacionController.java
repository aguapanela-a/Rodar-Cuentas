package co.edu.uni.rodar.cuentas.liquidaciones.controllers;

import co.edu.uni.rodar.cuentas.liquidaciones.dto.EstadoLiquidacionRequest;
import co.edu.uni.rodar.cuentas.liquidaciones.dto.LiquidacionResponse;
import co.edu.uni.rodar.cuentas.liquidaciones.services.LiquidacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LiquidacionController {

    private final LiquidacionService liquidacionService;

    /** Crea una liquidación pendiente para el usuario. No lleva cuerpo. */
    @PostMapping("/usuarios/{usuarioId}/liquidaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public LiquidacionResponse crear(@PathVariable("usuarioId") String cedula) {
        return liquidacionService.crear(cedula);
    }

    @GetMapping("/usuarios/{usuarioId}/liquidaciones")
    public List<LiquidacionResponse> listarPorUsuario(@PathVariable("usuarioId") String cedula) {
        return liquidacionService.listarPorUsuario(cedula);
    }

    @PatchMapping("/liquidaciones/{liquidacionId}")
    public LiquidacionResponse cambiarEstado(@PathVariable("liquidacionId") UUID liquidacionId,
                                             @Valid @RequestBody EstadoLiquidacionRequest solicitud) {
        return liquidacionService.cambiarEstado(liquidacionId, solicitud);
    }
}
