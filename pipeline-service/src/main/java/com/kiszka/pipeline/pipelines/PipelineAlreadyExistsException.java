package com.kiszka.pipeline.pipelines;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PipelineAlreadyExistsException extends RuntimeException {
    public PipelineAlreadyExistsException(String message) {
        super(message);
    }
}
