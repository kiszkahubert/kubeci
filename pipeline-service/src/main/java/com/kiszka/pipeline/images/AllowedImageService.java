package com.kiszka.pipeline.images;

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
public class AllowedImageService {

    private final AllowedImageRepository allowedImageRepository;

    public List<AllowedImageResponse> getAllImages() {
        return allowedImageRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AllowedImageResponse addImage(AllowedImageRequest request) {
        if (allowedImageRepository.existsByPattern(request.getPattern())) {
            throw new AllowedImageAlreadyExistsException("Pattern " + request.getPattern() + " already exists");
        }
        AllowedImage image = new AllowedImage();
        image.setPattern(request.getPattern());
        image.setDescription(request.getDescription());
        return mapToResponse(allowedImageRepository.save(image));
    }

    public void deleteImage(Long id) {
        if (!allowedImageRepository.existsById(id)) {
            throw new AllowedImageNotFoundException("Allowed image with ID " + id + " was not found");
        }
        allowedImageRepository.deleteById(id);
    }

    public void checkImageAllowed(String image) {
        for (AllowedImage allowedImage : allowedImageRepository.findAll()) {
            String pattern = allowedImage.getPattern();
            if (pattern.endsWith("*")) {
                String prefix = pattern.substring(0, pattern.length() - 1);
                if (image.startsWith(prefix)) {
                    return;
                }
            } else if (image.equals(pattern)) {
                return;
            }
        }
        throw new ImageNotAllowedException("Image " + image + " is not on the list of allowed images");
    }

    private AllowedImageResponse mapToResponse(AllowedImage image) {
        AllowedImageResponse response = new AllowedImageResponse();
        response.setId(image.getId());
        response.setPattern(image.getPattern());
        response.setDescription(image.getDescription());
        return response;
    }
}
