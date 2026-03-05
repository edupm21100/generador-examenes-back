package com.eduardo.examen_backend.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.eduardo.examen_backend.models.Incidencia;
import com.eduardo.examen_backend.repositories.IncidenciaRepository;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final IncidenciaRepository incidenciaRepository;

    public GlobalExceptionHandler(IncidenciaRepository incidenciaRepository) {
        this.incidenciaRepository = incidenciaRepository;
    }

    private void auditarIncidencia(Exception ex, HttpServletRequest request) {
        String endpoint = request.getMethod() + " " + request.getRequestURI();
        String tipo = ex.getClass().getSimpleName();

        String clase = "Desconocida";
        String metodo = "Desconocido";
        int linea = 0;

        for (StackTraceElement element : ex.getStackTrace()) {
            if (element.getClassName().startsWith("com.eduardo.examen_backend")
                    && !element.getMethodName().contains("$")) {
                clase = element.getClassName();
                metodo = element.getMethodName();
                linea = element.getLineNumber();
                break;
            }
        }

        String mensajeLimpio = ex.getMessage() != null ? ex.getMessage() : "Sin mensaje detallado";
        String traza = "Mensaje: " + mensajeLimpio + " | Fallo en línea: " + linea;

        if (traza.length() > 500) {
            traza = traza.substring(0, 497) + "...";
        }

        // TODO: Para cuando tengamos Spring Security
        Integer idUsuario = null;

        Incidencia incidencia = new Incidencia(endpoint, tipo, clase, metodo, traza, LocalDateTime.now(), idUsuario);
        incidenciaRepository.save(incidencia);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errorMessage);

        auditarIncidencia(ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage());
        auditarIncidencia(ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseExceptions(DataIntegrityViolationException ex,
            HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "Error de integridad en la base de datos. Es posible que el registro ya exista (ej. correo duplicado) o falten datos obligatorios.");
        auditarIncidencia(ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage());
        auditarIncidencia(ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ha ocurrido un error inesperado en el servidor, posiblemente error de escritura en el texto de la petición.");
        auditarIncidencia(ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateException(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage());
        auditarIncidencia(ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex,
            HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                ex.getMessage());
        auditarIncidencia(ex, request);
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}