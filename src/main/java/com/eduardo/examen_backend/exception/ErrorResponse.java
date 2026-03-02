package com.eduardo.examen_backend.exception;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estructura estándar para todas las respuestas de error de la API")
public class ErrorResponse {
    @Schema(description = "Fecha y hora exacta en la que ocurrió el error", example = "2026-03-02T13:45:12.345")
    private LocalDateTime timestamp;
    @Schema(description = "Código de estado HTTP", example = "404")
    private int status;
    @Schema(description = "Nombre oficial del error HTTP", example = "Not Found")
    private String error;
    @Schema(description = "Mensaje detallado para el desarrollador Front-end", example = "El usuario con ID 999 no existe")
    private String message;
    @Schema(description = "Ruta de la API que generó el error", example = "/usuarios/999")
    private String path;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }



}
