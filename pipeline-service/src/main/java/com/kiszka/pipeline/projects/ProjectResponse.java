package com.kiszka.pipeline.projects;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal cpuLimitCores;
    private Integer memoryLimitMb;
    private Integer maxConcurrentRuns;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
