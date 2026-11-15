package co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest;

import co.edu.uni.rodar.cuentas.dominio.excepciones.CorreoDuplicadoException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.DatosInvalidosException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.DominioException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.ReferenciaEnConflictoException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.SaldoInsuficienteException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.UsuarioInactivoException;
import co.edu.uni.rodar.cuentas.dominio.excepciones.UsuarioNoEncontradoException;
import co.edu.uni.rodar.cuentas.infraestructura.adaptadores.entrada.rest.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Traduce toda excepción al formato de error del clúster {codigo, mensaje, detalle}.
 * Extiende ResponseEntityExceptionHandler para cubrir también los errores propios de Spring MVC
 * (ruta inexistente, método no permitido, JSON mal formado...) con su código HTTP correcto.
 */
@RestControllerAdvice
public class ManejadorErrores extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ManejadorErrores.class);
    private static final String SOLICITUD_INVALIDA = "solicitud_invalida";

    // ---------- Errores de negocio ----------

    @ExceptionHandler(DominioException.class)
    public ResponseEntity<ErrorResponse> manejarDominio(DominioException ex) {
        HttpStatus estado = switch (ex) {
            case DatosInvalidosException e -> HttpStatus.BAD_REQUEST;
            case UsuarioNoEncontradoException e -> HttpStatus.NOT_FOUND;
            case CorreoDuplicadoException e -> HttpStatus.CONFLICT;
            case UsuarioInactivoException e -> HttpStatus.CONFLICT;
            case SaldoInsuficienteException e -> HttpStatus.CONFLICT;
            case ReferenciaEnConflictoException e -> HttpStatus.CONFLICT;
            default -> HttpStatus.CONFLICT;
        };
        return ResponseEntity.status(estado).body(new ErrorResponse(ex.codigo(), ex.getMessage(), ex.detalle()));
    }

    // ---------- Cualquier otro error: 500 sin exponer detalles internos ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarInesperado(Exception ex) {
        log.error("Error no controlado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("error_interno", "Ocurrió un error interno"));
    }

    // ---------- Errores de entrada detectados por Spring ----------

    /** Fallo de Jakarta Validation en el cuerpo (@Valid). */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        List<FieldError> errores = ex.getBindingResult().getFieldErrors().stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .toList();
        if (errores.isEmpty()) {
            return responder(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, "La solicitud no es válida", Map.of());
        }
        FieldError primero = errores.getFirst();
        return responder(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, primero.getDefaultMessage(),
                Map.of("campo", primero.getField()));
    }

    /** UUID, número o fecha con formato inválido en la ruta o en la query. */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
                                                        HttpStatusCode status, WebRequest request) {
        String campo = ex instanceof MethodArgumentTypeMismatchException m ? m.getName() : ex.getPropertyName();
        if (campo == null) {
            return responder(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, "Un parámetro tiene un valor inválido", Map.of());
        }
        return responder(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA,
                "El parámetro '" + campo + "' tiene un valor inválido", Map.of("campo", campo));
    }

    /** JSON mal formado, tipo incorrecto o campo no permitido (additionalProperties: false). */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        if (ex.getCause() instanceof UnrecognizedPropertyException noPermitido) {
            return responder(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA,
                    "El campo '" + noPermitido.getPropertyName() + "' no está permitido",
                    Map.of("campo", noPermitido.getPropertyName()));
        }
        if (ex.getCause() instanceof JacksonException jackson && !jackson.getPath().isEmpty()) {
            String campo = jackson.getPath().getLast().getPropertyName();
            if (campo != null) {
                return responder(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA,
                        "El campo '" + campo + "' tiene un valor inválido", Map.of("campo", campo));
            }
        }
        return responder(HttpStatus.BAD_REQUEST, SOLICITUD_INVALIDA, "El cuerpo de la solicitud no es válido", Map.of());
    }

    /** Resto de errores de Spring MVC (404 de ruta, 405, 415, parámetro faltante...). */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        if (body instanceof ErrorResponse) {
            return new ResponseEntity<>(body, headers, statusCode);
        }
        ErrorResponse error = switch (statusCode.value()) {
            case 400 -> new ErrorResponse(SOLICITUD_INVALIDA, "La solicitud no es válida");
            case 404 -> new ErrorResponse("recurso_no_encontrado", "El recurso solicitado no existe");
            case 405 -> new ErrorResponse("metodo_no_permitido", "Método HTTP no permitido para esta ruta");
            case 406 -> new ErrorResponse("formato_no_aceptable", "Formato de respuesta no soportado");
            case 415 -> new ErrorResponse("tipo_contenido_no_soportado", "El cuerpo debe enviarse como application/json");
            default -> statusCode.is5xxServerError()
                    ? new ErrorResponse("error_interno", "Ocurrió un error interno")
                    : new ErrorResponse(SOLICITUD_INVALIDA, "La solicitud no es válida");
        };
        if (statusCode.is5xxServerError()) {
            log.error("Error interno de Spring MVC", ex);
        }
        return new ResponseEntity<>(error, headers, statusCode);
    }

    private static ResponseEntity<Object> responder(HttpStatus estado, String codigo, String mensaje,
                                                    Map<String, Object> detalle) {
        return ResponseEntity.status(estado).body(new ErrorResponse(codigo, mensaje, detalle));
    }
}
