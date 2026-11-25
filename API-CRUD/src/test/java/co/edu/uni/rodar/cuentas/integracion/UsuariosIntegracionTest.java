package co.edu.uni.rodar.cuentas.integracion;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UsuariosIntegracionTest extends IntegracionBase {

    @Test
    void crearUsuarioCreaSuBilleteraConSaldoCero() throws Exception {
        String cedula = cedulaUnica();

        crearUsuario(cedula, correoUnico())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/usuarios/" + cedula)))
                .andExpect(jsonPath("$.cedula").value(cedula))
                .andExpect(jsonPath("$.nombreCompleto").value("Nicolas Castro"))
                .andExpect(jsonPath("$.estado").value("activo"));

        mvc.perform(get("/usuarios/{id}/billetera", cedula))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cedula").value(cedula))
                .andExpect(jsonPath("$.saldo").value(0));
        assertThat(saldoEnBd(cedula)).isZero();
    }

    @Test
    void nombreCompletoUneLosCuatroNombresConUnSoloEspacio() throws Exception {
        String cuerpo = """
                {"cedula": "%s", "primerNombre": " Nicolas ", "segundoNombre": "Andres",
                 "primerApellido": "Castro", "segundoApellido": "  ", "correo": "%s"}
                """.formatted(cedulaUnica(), correoUnico());

        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreCompleto").value("Nicolas Andres Castro"));
    }

    @Test
    void faltaUnCampoObligatorioResponde400() throws Exception {
        String cuerpo = """
                {"cedula": "%s", "primerNombre": "Nicolas", "correo": "%s"}
                """.formatted(cedulaUnica(), correoUnico());

        mvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("solicitud_invalida"))
                .andExpect(jsonPath("$.detalle.campo").value("primerApellido"));
    }

    @Test
    void correoRepetidoAlCrearResponde409AunqueCambienLasMayusculas() throws Exception {
        String correo = correoUnico();
        crearUsuario(cedulaUnica(), correo).andExpect(status().isCreated());

        crearUsuario(cedulaUnica(), correo.toUpperCase())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("correo_duplicado"));
    }

    @Test
    void correoRepetidoAlActualizarResponde409() throws Exception {
        String correoOcupado = correoUnico();
        crearUsuario(cedulaUnica(), correoOcupado).andExpect(status().isCreated());
        String cedula = crearUsuarioActivo();

        String cuerpo = """
                {"primerNombre": "Nicolas", "primerApellido": "Castro", "correo": "%s"}
                """.formatted(correoOcupado);
        mvc.perform(put("/usuarios/{id}", cedula).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("correo_duplicado"));
    }

    @Test
    void actualizarConSuPropioCorreoNoEsConflicto() throws Exception {
        String cedula = cedulaUnica();
        String correo = correoUnico();
        crearUsuario(cedula, correo).andExpect(status().isCreated());

        String cuerpo = """
                {"primerNombre": "Nicolas", "primerApellido": "Castro", "segundoApellido": "Rojas", "correo": "%s"}
                """.formatted(correo);
        mvc.perform(put("/usuarios/{id}", cedula).contentType(MediaType.APPLICATION_JSON).content(cuerpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreCompleto").value("Nicolas Castro Rojas"));
    }

    @Test
    void cedulaRepetidaResponde409SinSobrescribirAlUsuarioExistente() throws Exception {
        String cedula = cedulaUnica();
        String correoOriginal = correoUnico();
        crearUsuario(cedula, correoOriginal).andExpect(status().isCreated());

        crearUsuario(cedula, correoUnico())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("cedula_duplicada"));

        mvc.perform(get("/usuarios/{id}", cedula))
                .andExpect(jsonPath("$.correo").value(correoOriginal));
    }

    @Test
    void usuarioInexistenteResponde404() throws Exception {
        mvc.perform(get("/usuarios/{id}", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("usuario_no_encontrado"));
    }

    @Test
    void patchCambiaElEstadoYEsIdempotente() throws Exception {
        String cedula = crearUsuarioActivo();

        cambiarEstado(cedula, "inactivo")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("inactivo"));
        cambiarEstado(cedula, "inactivo")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("inactivo"));
        mvc.perform(get("/usuarios/{id}", cedula))
                .andExpect(jsonPath("$.estado").value("inactivo"));

        cambiarEstado(cedula, "ACTIVO")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("activo"));
    }

    @Test
    void patchConEstadoInvalidoResponde400YConUsuarioInexistente404() throws Exception {
        String cedula = crearUsuarioActivo();

        cambiarEstado(cedula, "borrado")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("solicitud_invalida"));
        cambiarEstado("999", "inactivo")
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteHaceBorradoLogico() throws Exception {
        String cedula = crearUsuarioActivo();

        mvc.perform(delete("/usuarios/{id}", cedula)).andExpect(status().isNoContent());

        mvc.perform(get("/usuarios/{id}", cedula))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("inactivo"));
    }

    @Test
    void listarFiltraPorCorreoYValidaLosFiltros() throws Exception {
        String cedula = cedulaUnica();
        String correo = correoUnico();
        crearUsuario(cedula, correo).andExpect(status().isCreated());

        mvc.perform(get("/usuarios").param("correo", correo).param("estado", "activo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.pagina").value(1))
                .andExpect(jsonPath("$.datos[0].cedula").value(cedula));

        mvc.perform(get("/usuarios").param("estado", "borrado"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/usuarios").param("tamano", "500"))
                .andExpect(status().isBadRequest());
    }
}
