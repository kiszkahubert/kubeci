package com.kiszka.pipeline.projects;

import com.kiszka.pipeline.pipelines.PipelineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final PipelineRepository pipelineRepository;

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project of ID " + id + " have not been changed"));
        return mapToResponse(project);
    }

    public List<ProjectResponse> searchProjects(String name) {
        return projectRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse createProject(ProjectRequest request) {
        if (projectRepository.existsByName(request.getName())) {
            throw new ProjectAlreadyExistsException("Project " + request.getName() + " already exists");
        }
        Project project = new Project();
        mapToEntity(request, project);
        Project savedProject = projectRepository.save(project);
        return mapToResponse(savedProject);
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project with ID " + id + " was not found"));
        if (!project.getName().equals(request.getName()) && projectRepository.existsByName(request.getName())) {
            throw new ProjectAlreadyExistsException("Project " + request.getName() + " already exists");
        }
        mapToEntity(request, project);
        Project updatedProject = projectRepository.saveAndFlush(project);
        return mapToResponse(updatedProject);
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException("Project with ID " + id + " was not found");
        }
        if (pipelineRepository.existsByProjectId(id)) {
            throw new ProjectNotEmptyException("Project with ID " + id + " has pipelines, delete them first");
        }
        projectRepository.deleteById(id);
    }

    private ProjectResponse mapToResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setCpuLimitCores(project.getCpuLimitCores());
        response.setMemoryLimitMb(project.getMemoryLimitMb());
        response.setMaxConcurrentRuns(project.getMaxConcurrentRuns());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());
        return response;
    }

    private void mapToEntity(ProjectRequest request, Project project) {
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setCpuLimitCores(request.getCpuLimitCores());
        project.setMemoryLimitMb(request.getMemoryLimitMb());
        project.setMaxConcurrentRuns(request.getMaxConcurrentRuns());
    }
}
