package com.kiszka.pipeline.runs;

import com.kiszka.pipeline.images.AllowedImageService;
import com.kiszka.pipeline.pipelines.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RunService {

    private final RunRepository runRepository;
    private final PipelineRepository pipelineRepository;
    private final AllowedImageService allowedImageService;

    public RunResponse startRun(Long pipelineId, String user) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new PipelineNotFoundException("Pipeline with ID " + pipelineId + " was not found"));
        if (!pipeline.getEnabled()) {
            throw new PipelineDisabledException("Pipeline " + pipeline.getName() + " is disabled");
        }
        for (Step step : pipeline.getSteps()) {
            allowedImageService.checkImageAllowed(step.getImage());
        }
        long activeRuns = runRepository.countByProjectAndStatuses(
                pipeline.getProject().getId(), List.of(RunStatus.QUEUED, RunStatus.RUNNING));
        if (activeRuns >= pipeline.getProject().getMaxConcurrentRuns()) {
            throw new RunLimitExceededException("Project " + pipeline.getProject().getName()
                    + " has reached the limit of active runs: " + pipeline.getProject().getMaxConcurrentRuns());
        }

        Run run = new Run();
        run.setPipeline(pipeline);
        run.setStatus(RunStatus.QUEUED);
        run.setTriggeredBy(user);
        for (Step step : pipeline.getSteps()) {
            StepRun stepRun = new StepRun();
            stepRun.setRun(run);
            stepRun.setOrderIndex(step.getOrderIndex());
            stepRun.setName(step.getName());
            stepRun.setImage(step.getImage());
            stepRun.setCommand(step.getCommand());
            stepRun.setStatus(StepStatus.PENDING);
            run.getStepRuns().add(stepRun);
        }
        Run savedRun = runRepository.save(run);
        return mapToResponse(savedRun);
    }

    public List<RunResponse> getRunsByPipeline(Long pipelineId) {
        if (!pipelineRepository.existsById(pipelineId)) {
            throw new PipelineNotFoundException("Pipeline with ID " + pipelineId + " was not found");
        }
        return runRepository.findByPipelineIdOrderByCreatedAtDesc(pipelineId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<RunResponse> getRunsByStatus(RunStatus status) {
        return runRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public RunResponse getRunById(Long id) {
        Run run = runRepository.findById(id)
                .orElseThrow(() -> new RunNotFoundException("Run with ID " + id + " was not found"));
        return mapToResponse(run);
    }

    public RunResponse cancelRun(Long id) {
        Run run = runRepository.findById(id)
                .orElseThrow(() -> new RunNotFoundException("Run with ID " + id + " was not found"));
        if (run.getStatus() != RunStatus.QUEUED) {
            throw new InvalidRunStateException("Only queued runs can be cancelled, this run has status " + run.getStatus());
        }
        run.setStatus(RunStatus.CANCELLED);
        run.setFinishedAt(LocalDateTime.now());
        for (StepRun stepRun : run.getStepRuns()) {
            stepRun.setStatus(StepStatus.SKIPPED);
        }
        Run cancelledRun = runRepository.saveAndFlush(run);
        return mapToResponse(cancelledRun);
    }

    private RunResponse mapToResponse(Run run) {
        RunResponse response = new RunResponse();
        response.setId(run.getId());
        response.setPipelineId(run.getPipeline().getId());
        response.setPipelineName(run.getPipeline().getName());
        response.setStatus(run.getStatus());
        response.setTriggeredBy(run.getTriggeredBy());
        response.setSteps(run.getStepRuns()
                .stream()
                .map(this::mapStepRunToResponse)
                .collect(Collectors.toList()));
        response.setCreatedAt(run.getCreatedAt());
        response.setStartedAt(run.getStartedAt());
        response.setFinishedAt(run.getFinishedAt());
        return response;
    }

    private StepRunResponse mapStepRunToResponse(StepRun stepRun) {
        StepRunResponse response = new StepRunResponse();
        response.setOrderIndex(stepRun.getOrderIndex());
        response.setName(stepRun.getName());
        response.setImage(stepRun.getImage());
        response.setCommand(stepRun.getCommand());
        response.setStatus(stepRun.getStatus());
        response.setExitCode(stepRun.getExitCode());
        response.setLog(stepRun.getLog());
        response.setStartedAt(stepRun.getStartedAt());
        response.setFinishedAt(stepRun.getFinishedAt());
        return response;
    }
}
