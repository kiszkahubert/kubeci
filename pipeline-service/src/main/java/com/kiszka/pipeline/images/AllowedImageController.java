package com.kiszka.pipeline.images;

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
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
@Slf4j
public class AllowedImageController {
    private final AllowedImageService allowedImageService;

    @GetMapping
    @Tag(name = "images", description = "List of allowed Docker images")
    @Operation(
            summary = "Retrieve all allowed images",
            description = "Returns a list of image patterns that can be used in pipeline steps"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of allowed images",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = AllowedImageResponse.class))
                            )}
            )
    })
    public ResponseEntity<List<AllowedImageResponse>> getAllImages() {
        return ResponseEntity.ok(allowedImageService.getAllImages());
    }

    @PostMapping
    @Tag(name = "images")
    @Operation(summary = "Add allowed image", description = "Adds an image pattern, e.g. node:20*")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201", description = "Allowed image added",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AllowedImageResponse.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation of request data failed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Pattern already exists",
                    content = @Content
            )
    })
    public ResponseEntity<AllowedImageResponse> addImage(@Valid @RequestBody AllowedImageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(allowedImageService.addImage(request));
    }

    @DeleteMapping("/{id}")
    @Tag(name = "images")
    @Operation(
            summary = "Delete allowed image",
            description = "Deletes the image pattern with the given ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Allowed image deleted",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Allowed image with given ID does not exist",
                    content = @Content
            )
    })
    public ResponseEntity<Void> deleteImage(
            @Parameter(description = "ID of the allowed image to delete", required = true, example = "1") @PathVariable Long id
    ) {
        allowedImageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}
