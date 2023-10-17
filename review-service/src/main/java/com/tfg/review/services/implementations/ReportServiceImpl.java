package com.tfg.review.services.implementations;

import com.tfg.review.dtos.ReportDTO;
import com.tfg.review.exceptions.NullEntityException;
import com.tfg.review.exceptions.ReportNotFoundException;
import com.tfg.review.exceptions.SLRNotFoundException;
import com.tfg.review.models.Report;
import com.tfg.review.models.SLR;
import com.tfg.review.repositories.ReportRepository;
import com.tfg.review.services.ReportService;
import com.tfg.review.services.SLRService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@AllArgsConstructor
@Service
public class ReportServiceImpl implements ReportService {

    private final SLRService SLRService;

    private final ReportRepository reportRepository;

    //--CRUD--//

    public List<Report> findAll(){

        return reportRepository.findAll();
    }

    public Optional<Report> findOne(Long reportId) {
        if(!reportRepository.existsById(reportId)) throw new ReportNotFoundException("Report doesn't exist");

        return reportRepository.findReportById(reportId);
    }

    public Report createAndSave(ReportDTO reportDTO, Long slrId){
        if(reportDTO==null) throw new NullEntityException("Report is null");
        if(reportRepository.existsById(slrId)) throw new SLRNotFoundException("Report already exists");

        SLR father = SLRService.findOne(slrId);
        Report report = ReportDTO.buildEntity(reportDTO);
        if(father.getReport() != null) throw new IllegalArgumentException("This SLR already has a report. Try editing it");

        father.setReport(report);
        report.setSlr(father);

        SLRService.update(father);
        reportRepository.save(report);
        return report;
    }

    public Report update(Report report) {
        if(report==null) throw new NullEntityException("Report is null");
        if(!reportRepository.existsById(report.getId())) throw new ReportNotFoundException("Report doesn't exist yet");

        return reportRepository.save(report);
    }

    public void delete(Long reportId, Long slrId){
        if(reportId==null || !reportRepository.existsById(reportId)) throw new ReportNotFoundException("Report ID doesn't exist");
        SLR father = SLRService.findOne(slrId);

        reportRepository.deleteById(reportId);
        father.setProtocol(null);
    }

    //--Other methods--//

}
