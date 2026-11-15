package com.example.midliquidacion.infraestructura.config;

import com.example.midliquidacion.dominio.servicios.VerificarRecarga;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionApp {

    // las clases del dominio que NO pueden ser inicializadas directamente por spring se inician aqui manualmente
    @Bean
    public VerificarRecarga verificarRecarga() {
        return new VerificarRecarga();
    }
}
