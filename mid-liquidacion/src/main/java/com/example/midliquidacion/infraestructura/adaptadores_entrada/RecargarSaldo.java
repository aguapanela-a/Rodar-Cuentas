package com.example.midliquidacion.infraestructura.adaptadores_entrada;

import com.example.midliquidacion.aplicacion.puertos.puertos_entrada.RecargarSaldoIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/recargar-saldo")
public class RecargarSaldo {
    @Autowired
    private RecargarSaldoIn recargarSaldoIn;

    // String porque solo necesita abrirnsesion de checkout y retornar url de stripe
    @PostMapping("/{idUsuario}")
    public ResponseEntity<String> recargarSaldo(
            @PathVariable String idUsuario,
            @RequestBody RecargarSaldoPeticion peticion) {
        return ResponseEntity.ok(recargarSaldoIn.recargarSaldo(Long.parseLong(idUsuario), peticion.valor()));
    }

    public record RecargarSaldoPeticion(
            BigDecimal valor
    ){}

}
