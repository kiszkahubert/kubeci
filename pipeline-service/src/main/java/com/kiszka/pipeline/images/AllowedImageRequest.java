package com.kiszka.pipeline.images;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AllowedImageRequest {

    @Schema(description = "Image name or a pattern ending with '*'", example = "node:20*")
    @NotBlank(message = "Pattern is required")
    @Size(max = 255, message = "Pattern must have at most 255 characters")
    @Pattern(regexp = "^[a-z0-9._/:-]+\\*?$", message = "Pattern may contain lowercase letters, digits, '.', '_', '/', ':', '-' and an optional '*' at the end")
    private String pattern;

    @Schema(description = "What the image is used for", example = "Node.js 20")
    @Size(max = 255, message = "Description must have at most 255 characters")
    private String description;
}