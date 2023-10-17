package com.tfg.review.dtos;

import com.tfg.review.models.Report;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ReportDTO {

    private Long id;
    @NotBlank
    private String resume;

    @NotBlank
    private String conclusions;

    @NotBlank
    private String analysis;

    private Long slrId;

    public static ReportDTO buildFromEntity(Report report){
        ReportDTO result = ReportDTO.builder().id(report.getId()).resume(report.getResume()).conclusions(report.getConclusions())
                .analysis(report.getAnalysis()).build();
        if(report.getSlr() != null){
            result.setSlrId(report.getSlr().getId());
        }
        return  result;
    }

    public static Report buildEntity(ReportDTO reportDTO){
        return Report.builder().resume(reportDTO.getResume()).conclusions(reportDTO.getConclusions())
                .analysis(reportDTO.getAnalysis()).build();
    }
}
