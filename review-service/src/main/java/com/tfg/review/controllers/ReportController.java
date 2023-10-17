package com.tfg.review.controllers;

import com.tfg.review.dtos.ReportDTO;
import com.tfg.review.exceptions.ReportNotFoundException;
import com.tfg.review.models.Report;
import com.tfg.review.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/review/report")
@AllArgsConstructor
@Tag(name = "Reporte de la revisión", description = "Esta API contiene las operaciones que pueden ser " +
        "realizadas sobre una instancia de reporte de la revisión.")
@SecurityRequirement(name="Bearer Authentication")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener todos los reportes existentes.",
            description = "Este método permite consultar a la base de datos todos los reportes de revisión que hay almacenados.",
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized or Invalid Token",
                            responseCode = "403",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            responseCode = "500"
                    )
            }
    )
    public ResponseEntity<List<ReportDTO>> getAll(){
        List<Report> reports = reportService.findAll();
        List<ReportDTO> result = reports.stream().map(report -> ReportDTO.buildFromEntity(report)).collect(Collectors.toList());

        if(reports.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener un reporte de la base de datos",
            description = "Este método permite obtener un reporte por su ID en la base de datos.",
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized or Invalid Token",
                            responseCode = "403",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            responseCode = "500"
                    )
            }
    )
    public ResponseEntity<ReportDTO> getOne(@PathVariable Long id){
        try {
            Report report = reportService.findOne(id).orElseThrow(() -> new ReportNotFoundException("Report not found"));
            ReportDTO result = ReportDTO.buildFromEntity(report);
            return ResponseEntity.ok(result);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Crear un nuevo reporte de la revisión",
            description = "Este método permite crear un nuevo reporte para la revisión indicada mediante el ID. En caso " +
                    "de que la revisión ya tenga un reporte asociado, denegará la operación con un error 400 Bad Request.",
            responses = {
                    @ApiResponse(
                            description = "CREATED",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Unauthorized or Invalid Token",
                            responseCode = "403",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            responseCode = "500"
                    )
            }
    )
    public ResponseEntity<ReportDTO> createReport(@PathParam("slrId") Long slrId, @Valid @RequestBody ReportDTO dto, BindingResult binding){
        if(binding.hasErrors()){
            return new ResponseEntity(binding.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            Report result = reportService.createAndSave(dto, slrId);
            return ResponseEntity.ok(ReportDTO.buildFromEntity(result));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/edit")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Editar un reporte existente.",
            description = "Este método permite editar un reporte que ya existe en la base de datos, seleccionando el reporte " +
                    "por su ID y aportando la nueva información.",
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized or Invalid Token",
                            responseCode = "403",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            responseCode = "500"
                    )
            }
    )
    public ResponseEntity<ReportDTO> editReport(@PathVariable Long id, @Valid @RequestBody ReportDTO dto, BindingResult binding){
        if(binding.hasErrors()){
            return new ResponseEntity(binding.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            Report result = reportService.findOne(id).orElseThrow(()-> new ReportNotFoundException("Report not found"));
            result.setResume(dto.getResume());
            result.setAnalysis(dto.getAnalysis());
            result.setConclusions(dto.getConclusions());

            reportService.update(result);
            return ResponseEntity.ok(ReportDTO.buildFromEntity(result));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("{id}/delete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Eliminar un reporte existente.",
            description = "Este método permite eliminar un reporte de la base de datos, seleccionando el reporte " +
                    "por su ID.",
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized or Invalid Token",
                            responseCode = "403",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            responseCode = "500"
                    )
            }
    )
    public ResponseEntity<String> deleteReport(@PathVariable Long id){
        try {
            Report report = reportService.findOne(id).orElseThrow(()-> new ReportNotFoundException("Report not found"));
            reportService.delete(id, report.getSlr().getId());
            return new ResponseEntity("Report deleted successfully", HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
