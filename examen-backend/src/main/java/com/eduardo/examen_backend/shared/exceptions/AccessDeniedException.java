package com.eduardo.examen_backend.shared.exceptions;

public class AccessDeniedException extends RuntimeException {
    
    public AccessDeniedException(String message) {
        super(message);
    }

}
