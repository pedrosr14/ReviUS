package com.tfg.review.services;

import com.tfg.review.dtos.ReportDTO;
import com.tfg.review.models.Report;


import java.util.List;
import java.util.Optional;

public interface ReportService {

    List<Report> findAll();

    Optional<Report> findOne(Long reportId);

    Report createAndSave(ReportDTO reportDTO, Long slrId);

    Report update(Report report);

    void delete(Long reportId, Long slrId);
}
