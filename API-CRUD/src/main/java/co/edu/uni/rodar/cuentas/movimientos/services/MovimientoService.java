package co.edu.uni.rodar.cuentas.movimientos.services;

import co.edu.uni.rodar.cuentas.billeteras.services.BilleteraService;
import co.edu.uni.rodar.cuentas.common.dto.PaginaResponse;
import co.edu.uni.rodar.cuentas.common.exceptions.ConflictoException;
import co.edu.uni.rodar.cuentas.common.exceptions.SolicitudInvalidaException;
import co.edu.uni.rodar.cuentas.common.util.TextoEnum;
import co.edu.uni.rodar.cuentas.movimientos.dto.HistorialMovimientosResponse;
import co.edu.uni.rodar.cuentas.movimientos.dto.MovimientoHistorialItem;
import co.edu.uni.rodar.cuentas.movimientos.dto.MovimientoRequest;
import co.edu.uni.rodar.cuentas.movimientos.dto.MovimientoResponse;
import co.edu.uni.rodar.cuentas.movimientos.models.Movimiento;
import co.edu.uni.rodar.cuentas.movimientos.models.TipoMovimiento;
import co.edu.uni.rodar.cuentas.movimientos.repositories.MovimientoRepository;
import co.edu.uni.rodar.cuentas.usuarios.models.Usuario;
import co.edu.uni.rodar.cuentas.usuarios.services.UsuarioService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final UsuarioService usuarioService;
    private final BilleteraService billeteraService;
    private final TransactionTemplate transaccion;

    public MovimientoService(MovimientoRepository movimientoRepository,
                             UsuarioService usuarioService,
                             BilleteraService billeteraService,
                             PlatformTransactionManager gestorTransacciones) {
        this.movimientoRepository = movimientoRepository;
        this.usuarioService = usuarioService;
        this.billeteraService = billeteraService;
        this.transaccion = new TransactionTemplate(gestorTransacciones);
    }

    /**
     * Registra una recarga o un débito. Todo ocurre en una transacción.
     * <p>
     * Carrera de idempotencia: si dos peticiones con la misma referencia llegan a la vez, ambas pasan
     * el paso 4 y la segunda choca con la restricción única al insertar. Esa transacción se revierte
     * completa (también el cambio de saldo) y en una transacción nueva se busca el movimiento que
     * registró la otra petición: se devuelve igual que en el paso 4. En PostgreSQL no se puede seguir
     * usando una transacción después de un error, por eso la segunda es una transacción aparte.
     */
    public ResultadoMovimiento registrar(String cedula, MovimientoRequest solicitud) {
        try {
            return transaccion.execute(estado -> registrarEnTransaccion(cedula, solicitud));
        } catch (DataIntegrityViolationException e) {
            TipoMovimiento tipo = TextoEnum.desdeTexto(TipoMovimiento.class, solicitud.tipo(), "tipo");
            String referencia = solicitud.referencia().trim();
            ResultadoMovimiento original = transaccion.execute(estado ->
                    buscarPorReferencia(cedula, tipo, solicitud.monto(), referencia).orElse(null));
            if (original == null) {
                throw e;
            }
            return original;
        }
    }

    private ResultadoMovimiento registrarEnTransaccion(String cedula, MovimientoRequest solicitud) {
        // 1 y 2. Usuario inexistente → 404; INACTIVO → 409 usuario_inactivo.
        Usuario usuario = usuarioService.obtenerActivo(cedula);

        // 3. C-HU1: monto mayor que cero.
        long monto = solicitud.monto();
        if (monto <= 0) {
            throw new SolicitudInvalidaException("El monto debe ser mayor que cero", "monto");
        }
        TipoMovimiento tipo = TextoEnum.desdeTexto(TipoMovimiento.class, solicitud.tipo(), "tipo");
        String referencia = solicitud.referencia().trim();

        // 4. C-HU3: referencia ya usada → el original (200) o 409 si los datos no coinciden.
        Optional<ResultadoMovimiento> original = buscarPorReferencia(cedula, tipo, monto, referencia);
        if (original.isPresent()) {
            return original.get();
        }

        // 5 y 6. C-HU2: con la billetera bloqueada; un débito mayor al saldo → 409 saldo_insuficiente.
        long cambio = tipo == TipoMovimiento.RECARGA ? monto : -monto;
        billeteraService.aplicarCambioDeSaldo(cedula, cambio);

        Movimiento movimiento = new Movimiento();
        movimiento.setUsuario(usuario);
        movimiento.setTipo(tipo);
        movimiento.setMonto(monto);
        movimiento.setReferencia(referencia);
        // saveAndFlush: si la referencia choca con la restricción única, falla aquí y no al confirmar.
        Movimiento guardado = movimientoRepository.saveAndFlush(movimiento);
        return new ResultadoMovimiento(MovimientoResponse.desde(guardado), true);
    }

    /** Si la referencia existe: mismo usuario, tipo y monto → el original; si no → 409 referencia_en_conflicto. */
    private Optional<ResultadoMovimiento> buscarPorReferencia(String cedula, TipoMovimiento tipo, long monto,
                                                              String referencia) {
        return movimientoRepository.findByReferencia(referencia).map(existente -> {
            boolean mismosDatos = existente.getUsuario().getCedula().equals(cedula)
                    && existente.getTipo() == tipo
                    && existente.getMonto() == monto;
            if (!mismosDatos) {
                throw new ConflictoException("referencia_en_conflicto",
                        "La referencia ya se usó con datos distintos", Map.of("referencia", referencia));
            }
            return new ResultadoMovimiento(MovimientoResponse.desde(existente), false);
        });
    }

    /** Historial paginado, más recientes primero. Funciona aunque el usuario esté INACTIVO. */
    @Transactional(readOnly = true)
    public HistorialMovimientosResponse historial(String cedula, String tipo, int pagina, int tamano) {
        usuarioService.obtenerExistente(cedula);

        PageRequest paginacion = PageRequest.of(pagina - 1, tamano);
        Page<Movimiento> movimientos = (tipo == null || tipo.isBlank())
                ? movimientoRepository.findByUsuario_CedulaOrderByCreadoEnDesc(cedula, paginacion)
                : movimientoRepository.findByUsuario_CedulaAndTipoOrderByCreadoEnDesc(cedula,
                        TextoEnum.desdeTexto(TipoMovimiento.class, tipo, "tipo"), paginacion);

        return new HistorialMovimientosResponse(cedula,
                PaginaResponse.desde(movimientos, MovimientoHistorialItem::desde));
    }
}
