package co.edu.uni.rodar.cuentas.billeteras.services;

import co.edu.uni.rodar.cuentas.billeteras.dto.BilleteraResponse;
import co.edu.uni.rodar.cuentas.billeteras.models.Billetera;
import co.edu.uni.rodar.cuentas.billeteras.repositories.BilleteraRepository;
import co.edu.uni.rodar.cuentas.common.exceptions.ConflictoException;
import co.edu.uni.rodar.cuentas.common.exceptions.RecursoNoEncontradoException;
import co.edu.uni.rodar.cuentas.common.exceptions.SaldoInsuficienteException;
import co.edu.uni.rodar.cuentas.usuarios.models.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class BilleteraService {

    private final BilleteraRepository billeteraRepository;

    /**
     * La llama UsuarioService al crear un usuario, dentro de su transacción.
     * Recibe la entidad (no solo la cédula) porque la billetera necesita la referencia al usuario
     * y este service no puede usar el repositorio de usuarios.
     */
    @Transactional
    public void crear(Usuario usuario) {
        if (billeteraRepository.existsByUsuario_Cedula(usuario.getCedula())) {
            throw new ConflictoException("billetera_duplicada", "El usuario ya tiene una billetera",
                    Map.of("cedula", usuario.getCedula()));
        }
        Billetera billetera = new Billetera();
        billetera.setUsuario(usuario);
        billetera.setSaldo(0L);
        billeteraRepository.save(billetera);
    }

    /** Saldo actual. Funciona aunque el usuario esté INACTIVO. */
    @Transactional(readOnly = true)
    public BilleteraResponse consultar(String cedula) {
        return billeteraRepository.findByUsuario_Cedula(cedula)
                .map(BilleteraResponse::desde)
                .orElseThrow(() -> RecursoNoEncontradoException.usuario(cedula));
    }

    /**
     * Solo para MovimientoService. Suma {@code cambio} al saldo (positivo = recarga, negativo = débito)
     * y retorna el saldo nuevo.
     * <p>
     * Bloquea la fila con SELECT ... FOR UPDATE: un segundo movimiento concurrente sobre la misma
     * billetera espera a que termine este y lee el saldo ya actualizado.
     * Exige una transacción abierta (MANDATORY): sin ella el bloqueo no serviría.
     * Nunca deja el saldo negativo: lanza 409 saldo_insuficiente y no modifica nada.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public long aplicarCambioDeSaldo(String cedula, long cambio) {
        Billetera billetera = billeteraRepository.findByCedulaForUpdate(cedula)
                .orElseThrow(() -> RecursoNoEncontradoException.usuario(cedula));

        long saldoActual = billetera.getSaldo();
        long saldoNuevo = Math.addExact(saldoActual, cambio);
        if (saldoNuevo < 0) {
            throw new SaldoInsuficienteException(saldoActual, Math.abs(cambio));
        }
        billetera.setSaldo(saldoNuevo);
        return saldoNuevo;
    }
}
