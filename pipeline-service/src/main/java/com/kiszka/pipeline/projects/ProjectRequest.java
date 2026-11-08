package com.kiszka.pipeline.projects;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProjectRequest {

    @Schema(description = "Project name", example = "shop")
    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 50, message = "Project name length 3-50")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Project name consists only of chars, numbers, '_' and '-'")
    private String name;

    @Schema(description = "Project description", example = "shop backend")
    @Size(max = 500, message = "Project max name length 500")
    private String description;

    @Schema(description = "CPU Limit required for each step", example = "1.0")
    @NotNull(message = "CPU Limit required")
    @DecimalMin(value = "0.1", message = "CPU limit more or equal 0.1")
    @DecimalMax(value = "4.0", message = "CPU limit less or equal  4.0")
    @Digits(integer = 1, fraction = 1, message = "CPU Limit should be defines as float")
    private BigDecimal cpuLimitCores;

    @Schema(description = "MEM limit for each step (MB)", example = "1024")
    @NotNull(message = "MEM limit is required")
    @Min(value = 128, message = "MEM limit is more or equal to 128 MB")
    @Max(value = 4096, message = "MEM limit is less or equal to 4096 MB")
    private Integer memoryLimitMb;

    @Schema(description = "Number of concurrent runs", example = "1")
    @NotNull(message = "Concurrent runs limit is required")
    @Min(value = 1, message = "Concurrent workflow limit must be more or equal 1")
    @Max(value = 5, message = "Concurrent workflow limit must be less or equal 5")
    private Integer maxConcurrentRuns;
}
