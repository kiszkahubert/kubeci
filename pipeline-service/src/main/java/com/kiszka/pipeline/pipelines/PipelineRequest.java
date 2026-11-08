package com.kiszka.pipeline.pipelines;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class PipelineRequest {

    @Schema(description = "ID of the project the pipeline belongs to", example = "1")
    @NotNull(message = "Project ID is required")
    private Long projectId;

    @Schema(description = "Pipeline name (must be unique within the project)", example = "build-backend")
    @NotBlank(message = "Pipeline name is required")
    @Size(min = 3, max = 60, message = "Pipeline name must have between 3 and 60 characters")
    private String name;

    @Schema(description = "Git repository URL", example = "https://github.com/example/example.git")
    @NotBlank(message = "Repository URL is required")
    @Size(max = 255, message = "Repository URL must have at most 255 characters")
    @Pattern(regexp = "^https://\\S+\\.git$", message = "Repository URL must have the form https://...git")
    private String repoUrl;

    @Schema(description = "Branch to build", example = "main")
    @NotBlank(message = "Branch is required")
    @Size(max = 100, message = "Branch name must have at most 100 characters")
    private String branch;

    @Schema(description = "Whether the pipeline can be run", example = "true")
    @NotNull(message = "Enabled flag is required")
    private Boolean enabled;

    @Schema(description = "List of steps")
    @NotEmpty(message = "Pipeline must have at least one step")
    @Size(max = 20, message = "Pipeline can have at most 20 steps")
    @Valid
    private List<StepRequest> steps;
}
