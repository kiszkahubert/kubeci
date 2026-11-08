package com.kiszka.pipeline.images;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AllowedImageAlreadyExistsException extends RuntimeException {
    public AllowedImageAlreadyExistsException(String message) {
        super(message);
    }
}
