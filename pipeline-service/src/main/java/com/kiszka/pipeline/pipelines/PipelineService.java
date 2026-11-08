package com.kiszka.pipeline.pipelines;

import com.kiszka.pipeline.images.AllowedImageService;
import com.kiszka.pipeline.projects.Project;
import com.kiszka.pipeline.projects.ProjectNotFoundException;
import com.kiszka.pipeline.projects.ProjectRepository;
import com.kiszka.pipeline.runs.RunRepository;
import com.kiszka.pipeline.runs.RunStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PipelineService {

    private final PipelineRepository pipelineRepository;
    private final ProjectRepository projectRepository;
    private final RunRepository runRepository;
    private final AllowedImageService allowedImageService;

    public List<PipelineResponse> getAllPipelines() {
        return pipelineRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PipelineResponse getPipelineById(Long id) {
        Pipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new PipelineNotFoundException("Pipeline with ID " + id + " was not found"));
        return mapToResponse(pipeline);
    }

    public List<PipelineResponse> getPipelinesByProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Project with ID " + projectId + " was not found");
        }
        return pipelineRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PipelineResponse> searchPipelines(String name) {
        return pipelineRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PipelineResponse> getEnabledPipelines() {
        return pipelineRepository.findEnabledPipelines()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<PipelineResponse> getPipelinesByImage(String image) {
        return pipelineRepository.findByStepImage(image)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PipelineResponse createPipeline(PipelineRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project with ID " + request.getProjectId() + " was not found"));
        if (pipelineRepository.existsByProjectIdAndName(project.getId(), request.getName())) {
            throw new PipelineAlreadyExistsException("Pipeline with name " + request.getName() + " already exists in this project");
        }
        checkSteps(request.getSteps());

        Pipeline pipeline = new Pipeline();
        pipeline.setProject(project);
        mapToEntity(request, pipeline);
        addSteps(request.getSteps(), pipeline);
        Pipeline savedPipeline = pipelineRepository.save(pipeline);
        return mapToResponse(savedPipeline);
    }

    public PipelineResponse updatePipeline(Long id, PipelineRequest request) {
        Pipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new PipelineNotFoundException("Pipeline with ID " + id + " was not found"));
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project with ID " + request.getProjectId() + " was not found"));

        boolean nameOrProjectChanged = !pipeline.getName().equals(request.getName()) || !pipeline.getProject().getId().equals(project.getId());
        if (nameOrProjectChanged && pipelineRepository.existsByProjectIdAndName(project.getId(), request.getName())) {
            throw new PipelineAlreadyExistsException("Pipeline with name " + request.getName() + " already exists in this project");
        }
        checkSteps(request.getSteps());
        pipeline.setProject(project);
        mapToEntity(request, pipeline);
        pipeline.getSteps().clear();
        pipelineRepository.flush();
        addSteps(request.getSteps(), pipeline);
        pipelineRepository.flush();
        return mapToResponse(pipeline);
    }

    public void deletePipeline(Long id) {
        if (!pipelineRepository.existsById(id)) {
            throw new PipelineNotFoundException("Pipeline with ID " + id + " was not found");
        }
        if (runRepository.existsByPipelineIdAndStatusIn(id, List.of(RunStatus.QUEUED, RunStatus.RUNNING))) {
            throw new PipelineInUseException("Pipeline with ID " + id + " has an active run");
        }
        pipelineRepository.deleteById(id);
    }

    private void checkSteps(List<StepRequest> steps) {
        Set<Integer> usedOrders = new HashSet<>();
        for (StepRequest step : steps) {
            if (!usedOrders.add(step.getOrderIndex())) {
                throw new InvalidStepOrderException("Step order " + step.getOrderIndex() + " is used by more than one step");
            }
            allowedImageService.checkImageAllowed(step.getImage());
        }
    }

    private void addSteps(List<StepRequest> stepRequests, Pipeline pipeline) {
        for (StepRequest stepRequest : stepRequests) {
            Step step = new Step();
            step.setPipeline(pipeline);
            step.setOrderIndex(stepRequest.getOrderIndex());
            step.setName(stepRequest.getName());
            step.setImage(stepRequest.getImage());
            step.setCommand(stepRequest.getCommand());
            step.setTimeoutSeconds(stepRequest.getTimeoutSeconds());
            pipeline.getSteps().add(step);
        }
    }

    private PipelineResponse mapToResponse(Pipeline pipeline) {
        PipelineResponse response = new PipelineResponse();
        response.setId(pipeline.getId());
        response.setProjectId(pipeline.getProject().getId());
        response.setProjectName(pipeline.getProject().getName());
        response.setName(pipeline.getName());
        response.setRepoUrl(pipeline.getRepoUrl());
        response.setBranch(pipeline.getBranch());
        response.setEnabled(pipeline.getEnabled());
        response.setSteps(pipeline.getSteps()
                .stream()
                .map(this::mapStepToResponse)
                .collect(Collectors.toList()));
        response.setCreatedAt(pipeline.getCreatedAt());
        response.setUpdatedAt(pipeline.getUpdatedAt());
        return response;
    }

    private StepResponse mapStepToResponse(Step step) {
        StepResponse response = new StepResponse();
        response.setId(step.getId());
        response.setOrderIndex(step.getOrderIndex());
        response.setName(step.getName());
        response.setImage(step.getImage());
        response.setCommand(step.getCommand());
        response.setTimeoutSeconds(step.getTimeoutSeconds());
        return response;
    }

    private void mapToEntity(PipelineRequest request, Pipeline pipeline) {
        pipeline.setName(request.getName());
        pipeline.setRepoUrl(request.getRepoUrl());
        pipeline.setBranch(request.getBranch());
        pipeline.setEnabled(request.getEnabled());
    }
}
