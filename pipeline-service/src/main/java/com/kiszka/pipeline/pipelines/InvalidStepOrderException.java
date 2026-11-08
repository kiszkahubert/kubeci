package com.kiszka.pipeline.pipelines;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidStepOrderException extends RuntimeException {
    public InvalidStepOrderException(String message) {
        super(message);
    }
}
