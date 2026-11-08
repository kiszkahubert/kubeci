package com.kiszka.pipeline.pipelines;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PipelineResponse {
    private Long id;
    private Long projectId;
    private String projectName;
    private String name;
    private String repoUrl;
    private String branch;
    private Boolean enabled;
    private List<StepResponse> steps;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
