package com.example.midliquidacion.infraestructura.excepciones;

import com.example.midliquidacion.dominio.excepciones.BusinessException;
import com.example.midliquidacion.dominio.excepciones.ExcepcionesLiquidacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ManejadorDeExcepcionesTest {
    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.standaloneSetup(new ControladorPrueba())
                .setControllerAdvice(new ManejadorDeExcepciones()).build();
    }

    @Test
    void respondeErroresDeNegocio() throws Exception {
        mvc.perform(get("/errores/monto"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("400: monto invalido"))
                .andExpect(jsonPath("$.mensaje").value("el monto debe ser positivo"));
        mvc.perform(get("/errores/usuario"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("usuario_no_encontrado"))
                .andExpect(jsonPath("$.mensaje").value("El usuario no existe"));
        mvc.perform(get("/errores/pago"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("pago_rechazado"))
                .andExpect(jsonPath("$.mensaje").value("La pasarela de pagos rechazó la transacción"));
    }

    @Test
    void conservaTratamientoDeExcepcionesLiquidacion() throws Exception {
        mvc.perform(get("/errores/anterior-invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("dato invalido"))
                .andExpect(jsonPath("$.detalle").value("detalle"));
        mvc.perform(get("/errores/anterior-conflicto"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("saldo insuficiente"))
                .andExpect(jsonPath("$.detalle").value("detalle"));
    }

    @RestController
    static class ControladorPrueba {
        @GetMapping("/errores/{tipo}")
        void error(@PathVariable String tipo) {
            switch (tipo) {
                case "monto" -> throw new BusinessException("400: monto invalido", "el monto debe ser positivo");
                case "usuario" -> throw new BusinessException("usuario_no_encontrado", "El usuario no existe");
                case "pago" -> throw new BusinessException("pago_rechazado", "La pasarela de pagos rechazó la transacción");
                case "anterior-invalido" -> throw new ExcepcionesLiquidacion("dato invalido", "error", "detalle");
                default -> throw new ExcepcionesLiquidacion("saldo insuficiente", "error", "detalle");
            }
        }
    }
}