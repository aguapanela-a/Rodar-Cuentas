package co.edu.uni.rodar.cuentas.common.controllers;

import co.edu.uni.rodar.cuentas.common.dto.SaludResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Prueba de humo del contrato (GET /health). */
@RestController
public class HealthController {

    @GetMapping("/health")
    public SaludResponse salud() {
        return new SaludResponse("ok", "cuentas-crud");
    }
}
