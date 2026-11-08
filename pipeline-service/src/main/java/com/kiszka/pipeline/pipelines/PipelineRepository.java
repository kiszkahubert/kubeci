package com.kiszka.pipeline.pipelines;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, Long> {
    List<Pipeline> findByProjectId(Long projectId);
    List<Pipeline> findByNameContainingIgnoreCase(String name);
    boolean existsByProjectId(Long projectId);
    boolean existsByProjectIdAndName(Long projectId, String name);
    @Query("SELECT p FROM Pipeline p WHERE p.enabled = true")
    List<Pipeline> findEnabledPipelines();
    @Query("SELECT DISTINCT p FROM Pipeline p JOIN p.steps s WHERE s.image = :image")
    List<Pipeline> findByStepImage(@Param("image") String image);
}
