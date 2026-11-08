package com.kiszka.pipeline.pipelines;

import lombok.Data;

@Data
public class StepResponse {
    private Long id;
    private Integer orderIndex;
    private String name;
    private String image;
    private String command;
    private Integer timeoutSeconds;
}
