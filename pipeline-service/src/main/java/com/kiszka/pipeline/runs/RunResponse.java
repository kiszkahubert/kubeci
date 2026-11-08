package com.kiszka.pipeline.runs;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RunResponse {
    private Long id;
    private Long pipelineId;
    private String pipelineName;
    private RunStatus status;
    private String triggeredBy;
    private List<StepRunResponse> steps;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}