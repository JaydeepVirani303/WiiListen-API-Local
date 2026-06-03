package com.wiilisten.repo;

import com.wiilisten.entity.PdfPasswordChangeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdfPasswordChangeHistoryRepository extends JpaRepository<PdfPasswordChangeHistory, Long> {
    List<PdfPasswordChangeHistory> findAllByOrderByChangedAtDesc();
}
