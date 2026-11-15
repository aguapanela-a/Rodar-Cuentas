package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest;

import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto.SaludResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private static final SaludResponse OK = new SaludResponse("ok", "cuentas-crud");

    @GetMapping("/health")
    public SaludResponse salud() {
        return OK;
    }
}
