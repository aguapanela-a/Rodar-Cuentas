package com.example.midliquidacion.infraestructura.adaptadores_salida;

import com.example.midliquidacion.aplicacion.puertos.puertos_salida.UsuariosCrudOut;
import com.example.midliquidacion.dominio.modelos.Usuario;
import java.util.Optional;

public class UsuariosCrudOutImpl implements UsuariosCrudOut {
    @Override
    public Optional<Usuario> obtenerUsuario(Long usuarioId) {
        // TODO: Implementar adaptador de salida en infraestructura
        throw new UnsupportedOperationException("Adaptador de usuarios pendiente de implementación");
    }
}
