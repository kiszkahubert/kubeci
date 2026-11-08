package com.kiszka.pipeline.runs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RunRepository extends JpaRepository<Run, Long> {
    List<Run> findByPipelineIdOrderByCreatedAtDesc(Long pipelineId);
    List<Run> findByStatus(RunStatus status);
    boolean existsByPipelineIdAndStatusIn(Long pipelineId, List<RunStatus> statuses);
    @Query("SELECT COUNT(r) FROM Run r WHERE r.pipeline.project.id = :projectId AND r.status IN :statuses")
    long countByProjectAndStatuses(@Param("projectId") Long projectId, @Param("statuses") List<RunStatus> statuses);
}
