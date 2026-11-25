package com.example.midliquidacion.infraestructura.config;

import com.example.midliquidacion.aplicacion.casosUso.CrearMovimientoImpl;
import com.example.midliquidacion.aplicacion.casosUso.RecargarSaldoImpl;
import com.example.midliquidacion.aplicacion.puertos.puertos_entrada.CrearMovimientoIn;
import com.example.midliquidacion.aplicacion.puertos.puertos_entrada.RecargarSaldoIn;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.CrearMovimientoOut;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.PasarelaPagosOut;
import com.example.midliquidacion.aplicacion.puertos.puertos_salida.UsuariosCrudOut;
import com.example.midliquidacion.dominio.servicios.VerificarRecarga;
import com.example.midliquidacion.infraestructura.adaptadores_salida.CrearMovimientoOutImpl;
import com.example.midliquidacion.infraestructura.adaptadores_salida.UsuariosCrudOutImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionApp {

    // las clases del dominio que NO pueden ser inicializadas directamente por spring se inician aqui manualmente
    @Bean
    public VerificarRecarga verificarRecarga() {
        return new VerificarRecarga();
    }

    @Bean
    public UsuariosCrudOut usuariosCrudOut() {
        return new UsuariosCrudOutImpl();
    }

    @Bean
    public CrearMovimientoIn crearMovimientoIn() {
        return new CrearMovimientoImpl();
    }

    @Bean
    public CrearMovimientoOut crearMovimientoOut() {
        return new CrearMovimientoOutImpl();
    }

    @Bean
    public RecargarSaldoIn recargarSaldoIn(VerificarRecarga verificarRecarga, UsuariosCrudOut usuariosCrud,
                                          PasarelaPagosOut pasarelaPagos) {
        return new RecargarSaldoImpl(verificarRecarga, usuariosCrud, pasarelaPagos);
    }
}