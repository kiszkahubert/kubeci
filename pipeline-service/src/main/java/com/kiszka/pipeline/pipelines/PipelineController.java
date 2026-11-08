package com.kiszka.pipeline.pipelines;

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
@RequestMapping("/api/v1/pipelines")
@RequiredArgsConstructor
@Slf4j
public class PipelineController {

    private final PipelineService pipelineService;

    @GetMapping
    @Tag(name = "pipelines", description = "Pipeline definitions and their steps")
    @Operation(
            summary = "Retrieve all pipelines",
            description = "Returns a list of PipelineResponse objects with their steps"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of all pipelines",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PipelineResponse.class))
                            )}
            )
    })
    public ResponseEntity<List<PipelineResponse>> getAllPipelines() {
        return ResponseEntity.ok(pipelineService.getAllPipelines());
    }

    @GetMapping("/{id}")
    @Tag(name = "pipelines")
    @Operation(summary = "Retrieve pipeline by ID",
            description = "Returns the pipeline with the given ID together with its steps"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pipeline found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PipelineResponse.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pipeline with given ID does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID is not a valid number",
                    content = @Content
            )
    })
    public ResponseEntity<PipelineResponse> getPipelineById(
            @Parameter(description = "ID of the pipeline", required = true, example = "1") @PathVariable Long id)
    {
        return ResponseEntity.ok(pipelineService.getPipelineById(id));
    }

    @GetMapping("/project/{projectId}")
    @Tag(name = "pipelines")
    @Operation(
            summary = "Retrieve pipelines of a project",
            description = "Returns pipelines that belong to the project with the given ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of project pipelines",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PipelineResponse.class))
                            )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project with given ID does not exist",
                    content = @Content
            )
    })
    public ResponseEntity<List<PipelineResponse>> getPipelinesByProject(
            @Parameter(description = "ID of the project", required = true, example = "1") @PathVariable Long projectId)
    {
        return ResponseEntity.ok(pipelineService.getPipelinesByProject(projectId));
    }

    @GetMapping("/search")
    @Tag(name = "pipelines")
    @Operation(
            summary = "Search pipelines by name",
            description = "Returns pipelines whose name contains the given text"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of matching pipelines",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PipelineResponse.class))
                            )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Missing name parameter",
                    content = @Content
            )
    })
    public ResponseEntity<List<PipelineResponse>> searchPipelines(
            @Parameter(description = "Text to search for in pipeline names", required = true, example = "build")
            @RequestParam String name)
    {
        return ResponseEntity.ok(pipelineService.searchPipelines(name));
    }

    @GetMapping("/enabled")
    @Tag(name = "pipelines")
    @Operation(
            summary = "Retrieve enabled pipelines",
            description = "Returns only pipelines with enabled = true"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of enabled pipelines",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PipelineResponse.class))
                            )}
            )
    })
    public ResponseEntity<List<PipelineResponse>> getEnabledPipelines() {
        return ResponseEntity.ok(pipelineService.getEnabledPipelines());
    }

    @GetMapping("/image")
    @Tag(name = "pipelines")
    @Operation(
            summary = "Retrieve pipelines using an image",
            description = "Returns pipelines in which at least one step uses the given image"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of pipelines",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PipelineResponse.class))
                            )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Missing image parameter",
                    content = @Content
            )
    })
    public ResponseEntity<List<PipelineResponse>> getPipelinesByImage(
            @Parameter(description = "Full image name", required = true, example = "maven:3.9-eclipse-temurin-21")
            @RequestParam String image) {
        return ResponseEntity.ok(pipelineService.getPipelinesByImage(image));
    }

    @PostMapping
    @Tag(name = "pipelines")
    @Operation(
            summary = "Create new pipeline",
            description = "Creates a pipeline with steps from PipelineRequest"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Pipeline created",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PipelineResponse.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation of request data failed or step order is repeated",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Pipeline with given name already exists in the project",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Step image is not allowed",
                    content = @Content
            )
    })
    public ResponseEntity<PipelineResponse> createPipeline(@Valid @RequestBody PipelineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pipelineService.createPipeline(request));
    }

    @PutMapping("/{id}")
    @Tag(name = "pipelines")
    @Operation(
            summary = "Update existing pipeline",
            description = "Replaces pipeline data and the whole list of steps"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pipeline updated",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PipelineResponse.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation of request data failed or step order is repeated",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pipeline or project does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Another pipeline in the project has this name",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Step image is not allowed",
                    content = @Content
            )
    })
    public ResponseEntity<PipelineResponse> updatePipeline(
            @Parameter(description = "ID of the pipeline to update", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody PipelineRequest request)
    {
        return ResponseEntity.ok(pipelineService.updatePipeline(id, request));
    }

    @DeleteMapping("/{id}")
    @Tag(name = "pipelines")
    @Operation(
            summary = "Delete pipeline",
            description = "Deletes the pipeline together with its steps and run history"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Pipeline deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pipeline with given ID does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Pipeline has an active run",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deletePipeline(
            @Parameter(description = "ID of the pipeline to delete", required = true, example = "1") @PathVariable Long id)
    {
        pipelineService.deletePipeline(id);
        return ResponseEntity.noContent().build();
    }
}
