package com.kiszka.pipeline.pipelines;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "steps")
@Getter
@Setter
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pipeline_id", nullable = false)
    private Pipeline pipeline;

    @NotNull(message = "Step order is required")
    @Min(value = 1, message = "Step order must be at least 1")
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @NotBlank(message = "Step name is required")
    @Size(max = 50, message = "Step name must have at most 50 characters")
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank(message = "Image is required")
    @Size(max = 255, message = "Image name must have at most 255 characters")
    @Column(nullable = false)
    private String image;

    @NotBlank(message = "Command is required")
    @Size(max = 2000, message = "Command must have at most 2000 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String command;

    @NotNull(message = "Timeout is required")
    @Min(value = 10, message = "Timeout must be at least 10 seconds")
    @Max(value = 3600, message = "Timeout must be at most 3600 seconds")
    @Column(name = "timeout_seconds", nullable = false)
    private Integer timeoutSeconds;
}
