package com.kiszka.pipeline.projects;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects")
@Data
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 50, message = "Project name length should be 3-50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Size(max = 500, message = "Description should have max 500 characters")
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "CPU limit is required")
    @DecimalMin(value = "0.1", message = "Minimum CPU limit value = 0.1")
    @DecimalMax(value = "4.0", message = "Maximum CPU limit value = 4.0")
    @Digits(integer = 1, fraction = 1, message = "CPU limit should be a float")
    @Column(name = "cpu_limit_cores", nullable = false, precision = 2, scale = 1)
    private BigDecimal cpuLimitCores;

    @NotNull(message = "MEM limit is required")
    @Min(value = 128, message = "Minimum MEM limit value = 128 MB")
    @Max(value = 4096, message = "Maximum MEM limit value = 4096 MB")
    @Column(name = "memory_limit_mb", nullable = false)
    private Integer memoryLimitMb;

    @NotNull(message = "Concurrent workflows limit is required")
    @Min(value = 1, message = "Concurrent workflow limit must be more or equal 1")
    @Max(value = 5, message = "Concurrent workflow limit must be less or equal 5")
    @Column(name = "max_concurrent_runs", nullable = false)
    private Integer maxConcurrentRuns;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
