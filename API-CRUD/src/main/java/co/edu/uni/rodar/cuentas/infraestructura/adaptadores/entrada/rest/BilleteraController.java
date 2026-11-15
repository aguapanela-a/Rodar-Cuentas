package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.ObtenerSaldoUseCase;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto.BilleteraResponse;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto.DtoMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class BilleteraController {

    private final ObtenerSaldoUseCase obtenerSaldo;

    public BilleteraController(ObtenerSaldoUseCase obtenerSaldo) {
        this.obtenerSaldo = obtenerSaldo;
    }

    @GetMapping("/usuarios/{id}/billetera")
    public BilleteraResponse obtener(@PathVariable UUID id) {
        return DtoMapper.aResponse(obtenerSaldo.obtener(id));
    }
}
