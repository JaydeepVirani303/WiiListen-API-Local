package com.wiilisten.repo;

import com.wiilisten.entity.PdfFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PdfRepository extends JpaRepository<PdfFile, Long> {
    Optional<PdfFile> findByFileUrl(String fileUrl);
}

