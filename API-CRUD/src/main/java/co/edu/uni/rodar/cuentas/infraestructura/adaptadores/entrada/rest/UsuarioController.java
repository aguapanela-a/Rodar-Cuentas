package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest;

import co.edu.uni.rodar.cuentas.aplicacion.puertos.entrada.GestionarUsuarioUseCase;
import co.edu.uni.rodar.cuentas.dominio.modelos.EstadoUsuario;
import co.edu.uni.rodar.cuentas.dominio.modelos.FiltroUsuarios;
import co.edu.uni.rodar.cuentas.dominio.modelos.Paginacion;
import co.edu.uni.rodar.cuentas.dominio.modelos.Usuario;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto.ActualizarUsuarioRequest;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto.CrearUsuarioRequest;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto.DtoMapper;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto.PaginaResponse;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final GestionarUsuarioUseCase gestionarUsuario;

    public UsuarioController(GestionarUsuarioUseCase gestionarUsuario) {
        this.gestionarUsuario = gestionarUsuario;
    }

    @GetMapping
    public PaginaResponse<UsuarioResponse> listar(@RequestParam(required = false) String estado,
                                                  @RequestParam(required = false) String correo,
                                                  @RequestParam(required = false) Integer pagina,
                                                  @RequestParam(required = false) Integer tamano) {
        FiltroUsuarios filtro = new FiltroUsuarios(estado == null ? null : EstadoUsuario.desde(estado), correo);
        return DtoMapper.aResponse(gestionarUsuario.listar(filtro, Paginacion.de(pagina, tamano)),
                DtoMapper::aResponse);
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody CrearUsuarioRequest request) {
        Usuario creado = gestionarUsuario.crear(DtoMapper.aComando(request));
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();
        return ResponseEntity.created(ubicacion).body(DtoMapper.aResponse(creado));
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable UUID id) {
        return DtoMapper.aResponse(gestionarUsuario.obtener(id));
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable UUID id, @Valid @RequestBody ActualizarUsuarioRequest request) {
        return DtoMapper.aResponse(gestionarUsuario.actualizar(id, DtoMapper.aComando(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable UUID id) {
        gestionarUsuario.desactivar(id);
    }
}
