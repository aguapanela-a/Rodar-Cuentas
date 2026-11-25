package com.example.midliquidacion.aplicacion.puertos.puertos_salida;

import com.example.midliquidacion.dominio.modelos.Usuario;
import java.util.Optional;

public interface UsuariosCrudOut {
    Optional<Usuario> obtenerUsuario(Long usuarioId);
}
