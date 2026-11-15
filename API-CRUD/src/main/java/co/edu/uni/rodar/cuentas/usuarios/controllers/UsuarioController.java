package co.edu.uni.rodar.cuentas.usuarios.controllers;

import co.edu.uni.rodar.cuentas.common.dto.PaginaResponse;
import co.edu.uni.rodar.cuentas.usuarios.dto.ActualizarUsuarioRequest;
import co.edu.uni.rodar.cuentas.usuarios.dto.CrearUsuarioRequest;
import co.edu.uni.rodar.cuentas.usuarios.dto.EstadoUsuarioDto;
import co.edu.uni.rodar.cuentas.usuarios.dto.UsuarioResponse;
import co.edu.uni.rodar.cuentas.usuarios.services.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody CrearUsuarioRequest solicitud) {
        UsuarioResponse creado = usuarioService.crear(solicitud);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.cedula())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }

    @GetMapping
    public PaginaResponse<UsuarioResponse> listar(
            @RequestParam(name = "estado", required = false) String estado,
            @RequestParam(name = "correo", required = false)
            @Email(message = "El correo no tiene un formato válido") String correo,
            @RequestParam(name = "pagina", defaultValue = "1")
            @Min(value = 1, message = "La página empieza en 1") int pagina,
            @RequestParam(name = "tamano", defaultValue = "20")
            @Min(value = 1, message = "El tamaño debe estar entre 1 y 100")
            @Max(value = 100, message = "El tamaño debe estar entre 1 y 100") int tamano) {
        return usuarioService.listar(estado, correo, pagina, tamano);
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable("id") String cedula) {
        return usuarioService.obtener(cedula);
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable("id") String cedula,
                                      @Valid @RequestBody ActualizarUsuarioRequest solicitud) {
        return usuarioService.actualizar(cedula, solicitud);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable("id") String cedula) {
        usuarioService.desactivar(cedula);
    }

    @PatchMapping("/{id}")
    public EstadoUsuarioDto cambiarEstado(@PathVariable("id") String cedula,
                                          @Valid @RequestBody EstadoUsuarioDto solicitud) {
        return usuarioService.cambiarEstado(cedula, solicitud);
    }
}
