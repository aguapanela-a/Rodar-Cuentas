package co.edu.uni.rodar.cuentas.infraestructura.configuracion;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Solo acepta peticiones que traigan la cabecera secreta que agrega el API Gateway.
 * La autenticación real (JWT, alcances) la hace el gateway; esto evita que alguien llame
 * al servicio saltándose el gateway. /health queda libre para la prueba de humo.
 *
 * Como corre antes de Spring MVC, el ManejadorErrores no lo cubre: escribe él mismo el 401.
 */
@Component
public class FiltroCabeceraGateway extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(FiltroCabeceraGateway.class);
    private static final String RUTA_SALUD = "/health";
    private static final String CUERPO_401 =
            "{\"codigo\":\"no_autenticado\",\"mensaje\":\"Falta la cabecera del gateway o no es válida\",\"detalle\":{}}";

    private final String cabecera;
    private final byte[] secreto;

    public FiltroCabeceraGateway(@Value("${gateway.secreto.cabecera}") String cabecera,
                                 @Value("${gateway.secreto.valor:}") String valor) {
        this.cabecera = cabecera;
        this.secreto = (valor == null || valor.isBlank()) ? null : valor.getBytes(StandardCharsets.UTF_8);
        if (this.secreto == null) {
            log.warn("GATEWAY_SECRET está vacío: el filtro de la cabecera {} NO bloquea peticiones. "
                    + "Úsese solo en desarrollo local.", cabecera);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        return secreto == null || RUTA_SALUD.equals(ruta);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String recibido = request.getHeader(cabecera);
        // Comparación en tiempo constante para no filtrar información del secreto por tiempos de respuesta.
        if (recibido != null && MessageDigest.isEqual(secreto, recibido.getBytes(StandardCharsets.UTF_8))) {
            chain.doFilter(request, response);
            return;
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(CUERPO_401);
    }
}
