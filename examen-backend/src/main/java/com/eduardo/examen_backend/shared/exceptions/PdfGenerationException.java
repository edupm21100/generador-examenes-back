package com.eduardo.examen_backend.shared.exceptions;

public class PdfGenerationException extends RuntimeException {

    public PdfGenerationException(String message) {
        super(message);
    }

}
