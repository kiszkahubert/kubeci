package com.kiszka.pipeline.pipelines;

import com.kiszka.pipeline.projects.Project;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pipelines")
@Getter
@Setter
public class Pipeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Project is required")
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @NotBlank(message = "Pipeline name is required")
    @Size(min = 3, max = 60, message = "Pipeline name must have between 3 and 60 characters")
    @Column(nullable = false, length = 60)
    private String name;

    @NotBlank(message = "Repository URL is required")
    @Size(max = 255, message = "Repository URL must have at most 255 characters")
    @Column(name = "repo_url", nullable = false)
    private String repoUrl;

    @NotBlank(message = "Branch is required")
    @Size(max = 100, message = "Branch name must have at most 100 characters")
    @Column(nullable = false, length = 100)
    private String branch;

    @NotNull(message = "Enabled flag is required")
    @Column(nullable = false)
    private Boolean enabled;

    @Valid
    @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Step> steps = new ArrayList<>();

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