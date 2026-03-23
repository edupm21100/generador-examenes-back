package com.eduardo.examen_backend.shared.exceptions;

public class MissingBodyException extends RuntimeException {

        public MissingBodyException(String message) {
        super(message);
    }

}
