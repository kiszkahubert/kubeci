package com.kiszka.pipeline.images;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "allowed_images")
@Data
public class AllowedImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Pattern is required")
    @Size(max = 255, message = "Pattern must have at most 255 characters")
    @Column(nullable = false, unique = true)
    private String pattern;

    @Size(max = 255, message = "Description must have at most 255 characters")
    @Column
    private String description;
}