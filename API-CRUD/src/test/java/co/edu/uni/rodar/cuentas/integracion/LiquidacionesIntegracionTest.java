package co.edu.uni.rodar.cuentas.integracion;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LiquidacionesIntegracionTest extends IntegracionBase {

    private ResultActions crearLiquidacion(String cedula) throws Exception {
        return mvc.perform(post("/usuarios/{id}/liquidaciones", cedula));
    }

    private ResultActions cambiarEstadoLiquidacion(String id, String estado) throws Exception {
        return mvc.perform(patch("/liquidaciones/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\": \"" + estado + "\"}"));
    }

    @Test
    void crearLiquidacionLaDejaPendiente() throws Exception {
        String cedula = crearUsuarioActivo();

        crearLiquidacion(cedula)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.cedula").value(cedula))
                .andExpect(jsonPath("$.estado").value("pendiente"));
    }

    @Test
    void crearLiquidacionDeUsuarioInexistenteResponde404() throws Exception {
        crearLiquidacion(cedulaUnica())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("usuario_no_encontrado"));
    }

    @Test
    void crearLiquidacionDeUsuarioInactivoResponde409() throws Exception {
        String cedula = crearUsuarioActivo();
        cambiarEstado(cedula, "inactivo").andExpect(status().isOk());

        crearLiquidacion(cedula)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("usuario_inactivo"));
    }

    @Test
    void consultarPorUsuarioDevuelveSusLiquidaciones() throws Exception {
        String cedula = crearUsuarioActivo();
        crearLiquidacion(cedula).andExpect(status().isCreated());
        crearLiquidacion(cedula).andExpect(status().isCreated());
        crearLiquidacion(crearUsuarioActivo()).andExpect(status().isCreated());

        mvc.perform(get("/usuarios/{id}/liquidaciones", cedula))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].cedula").value(cedula));
    }

    @Test
    void consultarUsuarioSinLiquidacionesDevuelveListaVacia() throws Exception {
        mvc.perform(get("/usuarios/{id}/liquidaciones", crearUsuarioActivo()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void consultarUsuarioInexistenteResponde404() throws Exception {
        mvc.perform(get("/usuarios/{id}/liquidaciones", cedulaUnica()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("usuario_no_encontrado"));
    }

    @Test
    void cambiarEstadoARealizadaYDeVueltaAPendiente() throws Exception {
        String cedula = crearUsuarioActivo();
        String respuesta = crearLiquidacion(cedula).andReturn().getResponse().getContentAsString();
        String id = JsonPath.read(respuesta, "$.id");

        cambiarEstadoLiquidacion(id, "REALIZADA")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.estado").value("realizada"));
        cambiarEstadoLiquidacion(id, "pendiente")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("pendiente"));
    }

    @Test
    void cambiarAEstadoDesconocidoResponde400() throws Exception {
        String respuesta = crearLiquidacion(crearUsuarioActivo()).andReturn().getResponse().getContentAsString();
        String id = JsonPath.read(respuesta, "$.id");

        cambiarEstadoLiquidacion(id, "cancelada")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("solicitud_invalida"))
                .andExpect(jsonPath("$.detalle.campo").value("estado"));
    }

    @Test
    void cambiarEstadoDeLiquidacionInexistenteResponde404() throws Exception {
        cambiarEstadoLiquidacion(UUID.randomUUID().toString(), "realizada")
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("liquidacion_no_encontrada"));
    }

    @Test
    void idDeLiquidacionQueNoEsUuidResponde400() throws Exception {
        cambiarEstadoLiquidacion("no-es-uuid", "realizada")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("solicitud_invalida"));
    }
}
