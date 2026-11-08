package com.kiszka.pipeline.runs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/runs")
@RequiredArgsConstructor
@Slf4j
public class RunController {

    private final RunService runService;

    @PostMapping("/pipeline/{pipelineId}")
    @Tag(name = "runs", description = "Starting pipelines and run history")
    @Operation(
            summary = "Start pipeline",
            description = "Creates a run with status QUEUED and its steps with status PENDING"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Run created",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RunResponse.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pipeline does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Pipeline is disabled",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Image of one of the steps is no longer allowed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Project has reached the limit of active runs",
                    content = @Content
            )
    })
    public ResponseEntity<RunResponse> startRun(
            @Parameter(description = "ID of the pipeline to start", required = true, example = "1") @PathVariable Long pipelineId,
            @Parameter(description = "User who starts the pipeline", example = "john.smith")
            @RequestParam(defaultValue = "anonymous") String user)
    {
        return ResponseEntity.status(HttpStatus.CREATED).body(runService.startRun(pipelineId, user));
    }

    @GetMapping("/pipeline/{pipelineId}")
    @Tag(name = "runs")
    @Operation(
            summary = "Retrieve pipeline run history",
            description = "Returns runs of the pipeline, newest first"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of runs",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = RunResponse.class))
                            )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pipeline does not exist",
                    content = @Content
            )
    })
    public ResponseEntity<List<RunResponse>> getRunsByPipeline(
            @Parameter(description = "ID of the pipeline", required = true, example = "1") @PathVariable Long pipelineId)
    {
        return ResponseEntity.ok(runService.getRunsByPipeline(pipelineId));
    }

    @GetMapping("/status/{status}")
    @Tag(name = "runs")
    @Operation(
            summary = "Retrieve runs by status",
            description = "Returns all runs with the given status"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of runs",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = RunResponse.class))
                            )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Unknown status",
                    content = @Content
            )
    })
    public ResponseEntity<List<RunResponse>> getRunsByStatus(
            @Parameter(description = "Run status", required = true, example = "QUEUED") @PathVariable RunStatus status)
    {
        return ResponseEntity.ok(runService.getRunsByStatus(status));
    }

    @GetMapping("/{id}")
    @Tag(name = "runs")
    @Operation(
            summary = "Retrieve run by ID",
            description = "Returns the run together with step results"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Run found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RunResponse.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Run with given ID does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ID is not a valid number",
                    content = @Content
            )
    })
    public ResponseEntity<RunResponse> getRunById(
            @Parameter(description = "ID of the run", required = true, example = "1") @PathVariable Long id)
    {
        return ResponseEntity.ok(runService.getRunById(id));
    }

    @PostMapping("/{id}/cancel")
    @Tag(name = "runs")
    @Operation(
            summary = "Cancel run",
            description = "Cancels a run that is waiting in the queue (QUEUED)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Run cancelled",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RunResponse.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Run with given ID does not exist",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Run is not queued",
                    content = @Content
            )
    })
    public ResponseEntity<RunResponse> cancelRun(
            @Parameter(description = "ID of the run to cancel", required = true, example = "1") @PathVariable Long id)
    {
        return ResponseEntity.ok(runService.cancelRun(id));
    }
}
