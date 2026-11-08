package com.kiszka.pipeline.images;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AllowedImageRepository extends JpaRepository<AllowedImage, Long> {
    boolean existsByPattern(String pattern);
}
