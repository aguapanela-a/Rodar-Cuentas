package co.edu.uni.rodar.cuentas.common.exceptions;

import co.edu.uni.rodar.cuentas.common.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/** Traduce todas las excepciones al formato de error del contrato: { "codigo", "mensaje", "detalle" }. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> manejarApi(ApiException ex) {
        return responder(ex.getEstadoHttp(), ex.getCodigo(), ex.getMessage(), ex.getDetalle());
    }

    /** @Valid sobre un @RequestBody. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarCuerpoInvalido(MethodArgumentNotValidException ex) {
        Map<String, Object> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return solicitudInvalida(errores);
    }

    /** Restricciones sobre @RequestParam / @PathVariable (pagina, tamano, correo...). */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> manejarParametrosInvalidos(HandlerMethodValidationException ex) {
        Map<String, Object> errores = new LinkedHashMap<>();
        for (ParameterValidationResult resultado : ex.getParameterValidationResults()) {
            String campo = resultado.getMethodParameter().getParameterName();
            resultado.getResolvableErrors()
                    .forEach(error -> errores.putIfAbsent(campo, error.getDefaultMessage()));
        }
        return solicitudInvalida(errores);
    }

    /** JSON mal formado, campos no permitidos (additionalProperties: false) o tipos incorrectos. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        return responder(HttpStatus.BAD_REQUEST, "solicitud_invalida",
                "El cuerpo de la petición no es un JSON válido o tiene campos no permitidos", Map.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        return responder(HttpStatus.BAD_REQUEST, "solicitud_invalida",
                "Valor inválido para " + ex.getName(), Map.of("campo", ex.getName()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> manejarParametroFaltante(MissingServletRequestParameterException ex) {
        return responder(HttpStatus.BAD_REQUEST, "solicitud_invalida",
                "Falta el parámetro " + ex.getParameterName(), Map.of("campo", ex.getParameterName()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> manejarTipoContenido(HttpMediaTypeNotSupportedException ex) {
        return responder(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "tipo_contenido_no_soportado",
                "El cuerpo debe enviarse como application/json", Map.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> manejarRutaInexistente(NoResourceFoundException ex) {
        return responder(HttpStatus.NOT_FOUND, "recurso_no_encontrado", "La ruta no existe", Map.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> manejarMetodo(HttpRequestMethodNotSupportedException ex) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "metodo_no_permitido",
                "Método " + ex.getMethod() + " no permitido en esta ruta", Map.of());
    }

    /**
     * Restricción de la BD violada (único, FK, CHECK). Normalmente los services lo detectan antes;
     * esto cubre las carreras entre dos peticiones simultáneas.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarIntegridad(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad: {}", ex.getMostSpecificCause().getMessage());
        return responder(HttpStatus.CONFLICT, "conflicto",
                "La operación entra en conflicto con datos existentes", Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarInesperado(Exception ex) {
        log.error("Error no controlado", ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "error_interno", "Error interno del servidor", Map.of());
    }

    private ResponseEntity<ErrorResponse> solicitudInvalida(Map<String, Object> errores) {
        Map.Entry<String, Object> primero = errores.entrySet().iterator().next();
        Map<String, Object> detalle = new LinkedHashMap<>();
        detalle.put("campo", primero.getKey());
        detalle.put("errores", errores);
        return responder(HttpStatus.BAD_REQUEST, "solicitud_invalida", String.valueOf(primero.getValue()), detalle);
    }

    private ResponseEntity<ErrorResponse> responder(HttpStatus estado, String codigo, String mensaje,
                                                    Map<String, Object> detalle) {
        return ResponseEntity.status(estado).body(new ErrorResponse(codigo, mensaje, detalle));
    }
}
