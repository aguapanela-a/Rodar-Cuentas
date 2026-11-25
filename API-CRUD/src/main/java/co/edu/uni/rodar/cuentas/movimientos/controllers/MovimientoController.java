package co.edu.uni.rodar.cuentas.movimientos.controllers;

import co.edu.uni.rodar.cuentas.movimientos.dto.HistorialMovimientosResponse;
import co.edu.uni.rodar.cuentas.movimientos.dto.MovimientoRequest;
import co.edu.uni.rodar.cuentas.movimientos.dto.MovimientoResponse;
import co.edu.uni.rodar.cuentas.movimientos.services.MovimientoService;
import co.edu.uni.rodar.cuentas.movimientos.services.ResultadoMovimiento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios/{usuarioId}/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    /** 201 si se registró; 200 si la referencia ya existía y se devuelve el original. */
    @PostMapping
    public ResponseEntity<MovimientoResponse> registrar(@PathVariable("usuarioId") String cedula,
                                                        @Valid @RequestBody MovimientoRequest solicitud) {
        ResultadoMovimiento resultado = movimientoService.registrar(cedula, solicitud);
        HttpStatus estado = resultado.creado() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(estado).body(resultado.movimiento());
    }

    @GetMapping
    public HistorialMovimientosResponse historial(
            @PathVariable("usuarioId") String cedula,
            @RequestParam(name = "tipo", required = false) String tipo,
            @RequestParam(name = "pagina", defaultValue = "1")
            @Min(value = 1, message = "La página empieza en 1") int pagina,
            @RequestParam(name = "tamano", defaultValue = "20")
            @Min(value = 1, message = "El tamaño debe estar entre 1 y 100")
            @Max(value = 100, message = "El tamaño debe estar entre 1 y 100") int tamano) {
        return movimientoService.historial(cedula, tipo, pagina, tamano);
    }
}
