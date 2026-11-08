package com.kiszka.pipeline.projects;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    @Tag(name = "projects", description = "Project management")
    @Operation(
            summary = "Retrieve all projects",
            description = "Returns a list of ProjectResponse objects"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of all projects",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ProjectResponse.class))
                            )}
            )
    })
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    @Tag(name = "projects")
    @Operation(
            summary = "Retrieve project by ID",
            description = "Returns a single project with the given ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectResponse.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project with given ID does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID is not a valid number",
                    content = @Content
            )
    })
    public ResponseEntity<ProjectResponse> getProjectById(
            @Parameter(description = "ID of the project", required = true, example = "1") @PathVariable Long id)
    {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping("/search")
    @Tag(name = "projects")
    @Operation(
            summary = "Search projects by name",
            description = "Returns projects whose name contains the given text"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of matching projects",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ProjectResponse.class))
                            )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Missing name parameter",
                    content = @Content
            )
    })
    public ResponseEntity<List<ProjectResponse>> searchProjects(
            @Parameter(description = "Text to search for in project names", required = true, example = "shop")
            @RequestParam String name)
    {
        return ResponseEntity.ok(projectService.searchProjects(name));
    }

    @PostMapping
    @Tag(name = "projects")
    @Operation(
            summary = "Create new project",
            description = "Creates a new project from ProjectRequest and returns the created ProjectResponse"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Project created",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectResponse.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation of request data failed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Project with given name already exists",
                    content = @Content
            )
    })
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(request));
    }

    @PutMapping("/{id}")
    @Tag(name = "projects")
    @Operation(
            summary = "Update existing project",
            description = "Updates the project with the given ID using data from ProjectRequest"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Project updated",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectResponse.class)
                            )}),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation of request data failed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project with given ID does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Another project with given name already exists",
                    content = @Content
            )
    })
    public ResponseEntity<ProjectResponse> updateProject(
            @Parameter(description = "ID of the project to update", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request)
    {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    @Tag(name = "projects")
    @Operation(
            summary = "Delete project",
            description = "Deletes the project with the given ID if it has no pipelines"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Project deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project with given ID does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Project still has pipelines",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "ID of the project to delete", required = true, example = "1") @PathVariable Long id)
    {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
