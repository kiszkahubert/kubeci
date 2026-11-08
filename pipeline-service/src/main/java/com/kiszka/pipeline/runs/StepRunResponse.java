package com.kiszka.pipeline.runs;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StepRunResponse {
    private Integer orderIndex;
    private String name;
    private String image;
    private String command;
    private StepStatus status;
    private Integer exitCode;
    private String log;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}