package com.kiszka.pipeline.runs;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "step_runs")
@Getter
@Setter
public class StepRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "run_id", nullable = false)
    private Run run;

    @NotNull(message = "Step order is required")
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @NotBlank(message = "Step name is required")
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank(message = "Image is required")
    @Column(nullable = false)
    private String image;

    @NotBlank(message = "Command is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String command;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StepStatus status;

    @Column(name = "exit_code")
    private Integer exitCode;

    @Column(columnDefinition = "TEXT")
    private String log;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;
}