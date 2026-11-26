package com.example.midliquidacion.infraestructura.adaptadores_entrada;

import com.example.midliquidacion.aplicacion.puertos.puertos_entrada.LiquidarViajeIn;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//Controller para liquidaciones, tiene el metodo post para liquidar

@RestController
@RequestMapping("/liquidaciones")
public class LiquidarViaje {
    @Autowired
    private LiquidarViajeIn liquidarViajeIn;

    
    @PostMapping
    public ResponseEntity<LiquidacionResponse> l(
            @RequestBody LiquidarViajePeticion peticion) {
        return ResponseEntity.ok(liquidarViajeIn.liquidarViaje(peticion.viajeId()));
    }

    public record LiquidarViajePeticion(
            String viajeId
    ){}

}
