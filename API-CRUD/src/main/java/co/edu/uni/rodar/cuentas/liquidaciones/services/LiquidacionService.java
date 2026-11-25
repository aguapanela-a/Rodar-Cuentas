package co.edu.uni.rodar.cuentas.liquidaciones.services;

import co.edu.uni.rodar.cuentas.common.exceptions.RecursoNoEncontradoException;
import co.edu.uni.rodar.cuentas.common.util.TextoEnum;
import co.edu.uni.rodar.cuentas.liquidaciones.dto.EstadoLiquidacionRequest;
import co.edu.uni.rodar.cuentas.liquidaciones.dto.LiquidacionResponse;
import co.edu.uni.rodar.cuentas.liquidaciones.models.EstadoLiquidacion;
import co.edu.uni.rodar.cuentas.liquidaciones.models.Liquidacion;
import co.edu.uni.rodar.cuentas.liquidaciones.repositories.LiquidacionRepository;
import co.edu.uni.rodar.cuentas.usuarios.models.Usuario;
import co.edu.uni.rodar.cuentas.usuarios.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LiquidacionService {

    private final LiquidacionRepository liquidacionRepository;
    private final UsuarioService usuarioService;

    /** Crea una liquidación PENDIENTE. El usuario debe existir (404) y estar ACTIVO (409 usuario_inactivo). */
    @Transactional
    public LiquidacionResponse crear(String cedula) {
        Usuario usuario = usuarioService.obtenerActivo(cedula);

        Liquidacion liquidacion = new Liquidacion();
        liquidacion.setUsuario(usuario);
        liquidacion.setEstado(EstadoLiquidacion.PENDIENTE);
        return LiquidacionResponse.desde(liquidacionRepository.save(liquidacion));
    }

    /** Liquidaciones del usuario. Funciona aunque esté INACTIVO; lista vacía si no tiene. */
    @Transactional(readOnly = true)
    public List<LiquidacionResponse> listarPorUsuario(String cedula) {
        usuarioService.obtenerExistente(cedula);
        return liquidacionRepository.findAllByUsuario_Cedula(cedula).stream()
                .map(LiquidacionResponse::desde)
                .toList();
    }

    /** Cambia a PENDIENTE o REALIZADA. Si ya tiene ese estado no cambia nada. */
    @Transactional
    public LiquidacionResponse cambiarEstado(UUID liquidacionId, EstadoLiquidacionRequest solicitud) {
        Liquidacion liquidacion = liquidacionRepository.findById(liquidacionId)
                .orElseThrow(() -> RecursoNoEncontradoException.liquidacion(liquidacionId));
        EstadoLiquidacion nuevoEstado = TextoEnum.desdeTexto(EstadoLiquidacion.class, solicitud.estado(), "estado");
        if (liquidacion.getEstado() != nuevoEstado) {
            liquidacion.setEstado(nuevoEstado);
        }
        return LiquidacionResponse.desde(liquidacion);
    }
}
