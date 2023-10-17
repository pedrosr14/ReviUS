package com.tfg.review.repositories;

import com.tfg.review.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Override
    boolean existsById(Long id);

    Optional<Report> findReportById(Long id);
}