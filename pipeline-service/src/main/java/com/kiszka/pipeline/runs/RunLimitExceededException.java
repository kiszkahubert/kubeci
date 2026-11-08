package com.kiszka.pipeline.runs;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class RunLimitExceededException extends RuntimeException {
    public RunLimitExceededException(String message) {
        super(message);
    }
}