package com.kiszka.pipeline.projects;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProjectNotEmptyException extends RuntimeException {
    public ProjectNotEmptyException(String message) {
        super(message);
    }
}