package com.kiszka.pipeline.pipelines;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PipelineInUseException extends RuntimeException {
    public PipelineInUseException(String message) {
        super(message);
    }
}