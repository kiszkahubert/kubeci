package com.kiszka.pipeline.pipelines;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PipelineDisabledException extends RuntimeException {
    public PipelineDisabledException(String message) {
        super(message);
    }
}
