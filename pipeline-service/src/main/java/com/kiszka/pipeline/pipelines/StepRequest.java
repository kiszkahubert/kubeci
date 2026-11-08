package com.kiszka.pipeline.pipelines;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class StepRequest {

    @Schema(description = "Step order (starting from 1)", example = "1")
    @NotNull(message = "Step order is required")
    @Min(value = 1, message = "Step order must be at least 1")
    private Integer orderIndex;

    @Schema(description = "Step name", example = "build")
    @NotBlank(message = "Step name is required")
    @Size(max = 50, message = "Step name must have at most 50 characters")
    private String name;

    @Schema(description = "Docker image", example = "maven:3.9-eclipse-temurin-21")
    @NotBlank(message = "Image is required")
    @Size(max = 255, message = "Image name must have at most 255 characters")
    @Pattern(regexp = "^[a-z0-9._/:-]+$", message = "Invalid image name, e.g. maven:3.9-eclipse-temurin-21")
    private String image;

    @Schema(description = "Command to execute", example = "mvn -B package -DskipTests")
    @NotBlank(message = "Command is required")
    @Size(max = 2000, message = "Command must have at most 2000 characters")
    private String command;

    @Schema(description = "Maximum step duration in seconds", example = "600")
    @NotNull(message = "Timeout is required")
    @Min(value = 10, message = "Timeout must be at least 10 seconds")
    @Max(value = 3600, message = "Timeout must be at most 3600 seconds")
    private Integer timeoutSeconds;
}
