package com.kiszka.pipeline.images;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AllowedImageNotFoundException extends RuntimeException {
    public AllowedImageNotFoundException(String message) {
        super(message);
    }
}
