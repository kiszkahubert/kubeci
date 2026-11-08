package com.kiszka.pipeline.images;

import lombok.Data;

@Data
public class AllowedImageResponse {
    private Long id;
    private String pattern;
    private String description;
}
