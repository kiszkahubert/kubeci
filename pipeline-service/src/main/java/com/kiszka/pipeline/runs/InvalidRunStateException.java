package com.kiszka.pipeline.runs;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidRunStateException extends RuntimeException {
    public InvalidRunStateException(String message) {
        super(message);
    }
}