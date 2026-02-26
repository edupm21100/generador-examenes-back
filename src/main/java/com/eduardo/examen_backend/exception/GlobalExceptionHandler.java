package com.eduardo.examen_backend.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(NotFoundException exception, HttpServletRequest request) {
        System.out.println(
                "No has puesto algo bien, no sé, dímelo tú, ¿uh? ¿Qué no sabes? ¿Qué no se puede poner esto en código? Pero con lo divertido qué es. Imaginate corregir doce o quince ejercicios distintos, ¿no te gustaría leer algo distinto? ¿Qué no es serio? ¿Qué lo borre? Venga, anda, qué tontería, ¿no?");

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
