package co.edu.uni.rodar.cuentas.billeteras.controllers;

import co.edu.uni.rodar.cuentas.billeteras.dto.BilleteraResponse;
import co.edu.uni.rodar.cuentas.billeteras.services.BilleteraService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios/{id}/billetera")
@RequiredArgsConstructor
public class BilleteraController {

    private final BilleteraService billeteraService;

    @GetMapping
    public BilleteraResponse consultar(@PathVariable("id") String cedula) {
        return billeteraService.consultar(cedula);
    }
}
