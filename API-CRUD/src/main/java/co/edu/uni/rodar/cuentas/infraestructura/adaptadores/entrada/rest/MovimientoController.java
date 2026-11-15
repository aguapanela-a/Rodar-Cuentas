package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.ConsultarMovimientosUseCase;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.RegistrarMovimientoUseCase;
import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.ResultadoMovimiento;
import co.edu.uni.rodar.cuentas.dominio.modelos.FiltroMovimientos;
import co.edu.uni.rodar.cuentas.dominio.modelos.Paginacion;
import co.edu.uni.rodar.cuentas.dominio.modelos.TipoMovimiento;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto.DtoMapper;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto.MovimientoResponse;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto.PaginaResponse;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto.RegistrarMovimientoRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/billeteras/{usuarioId}/movimientos")
public class MovimientoController {

    private final RegistrarMovimientoUseCase registrarMovimiento;
    private final ConsultarMovimientosUseCase consultarMovimientos;

    public MovimientoController(RegistrarMovimientoUseCase registrarMovimiento,
                                ConsultarMovimientosUseCase consultarMovimientos) {
        this.registrarMovimiento = registrarMovimiento;
        this.consultarMovimientos = consultarMovimientos;
    }

    @GetMapping
    public PaginaResponse<MovimientoResponse> listar(
            @PathVariable UUID usuarioId,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta,
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer tamano) {
        FiltroMovimientos filtro = new FiltroMovimientos(tipo == null ? null : TipoMovimiento.desde(tipo), desde, hasta);
        return DtoMapper.aResponse(consultarMovimientos.listar(usuarioId, filtro, Paginacion.de(pagina, tamano)),
                DtoMapper::aResponse);
    }

    /** 201 si el movimiento es nuevo; 200 si la referencia ya existía con los mismos datos (idempotencia). */
    @PostMapping
    public ResponseEntity<MovimientoResponse> registrar(@PathVariable UUID usuarioId,
                                                        @Valid @RequestBody RegistrarMovimientoRequest request) {
        ResultadoMovimiento resultado = registrarMovimiento.registrar(DtoMapper.aComando(usuarioId, request));
        HttpStatus estado = resultado.yaExistia() ? HttpStatus.OK : HttpStatus.CREATED;
        return ResponseEntity.status(estado).body(DtoMapper.aResponse(resultado.movimiento()));
    }
}
