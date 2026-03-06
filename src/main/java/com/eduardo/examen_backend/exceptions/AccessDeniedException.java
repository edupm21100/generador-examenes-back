package com.eduardo.examen_backend.exceptions;

public class AccessDeniedException extends RuntimeException {
    
    public AccessDeniedException(String message) {
        super(message);
    }

}
