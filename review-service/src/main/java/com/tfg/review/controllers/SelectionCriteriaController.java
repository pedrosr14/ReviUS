package com.tfg.review.controllers;

import com.tfg.review.exceptions.SelectionCriteriaNotFoundException;
import com.tfg.review.models.SelectionCriteria;
import com.tfg.review.services.SelectionCriteriaService;
import com.tfg.review.utils.MessageConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/selection-criteria")
@Validated
@AllArgsConstructor
@Tag(name = "Criterios de selección", description = "Esta API contiene las operaciones que pueden ser " +
        "realizadas con los criterios de selección de un protocolo.")
@SecurityRequirement(name="Bearer Authentication")
public class SelectionCriteriaController {

    private SelectionCriteriaService selectionCriteriaService;


    @GetMapping("/get-applied-criteria")
    @Operation(
            summary = "Obtener los criterios de selección o extracción que se han aplicado a un estudio.",
            description = "Este método nos permite visualizar todos los criterios de selección o extracción que se han aplicado a un " +
                    "estudio durante la fase de selección.",
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
    public ResponseEntity<List<SelectionCriteria>> getAppliedCriteria(@RequestBody List<Long> criteriaIdList){
        try{
            if(criteriaIdList.isEmpty()){
                throw new SelectionCriteriaNotFoundException(MessageConstants.NO_SELECTION_CRITERIA_APPLIED);
            }
            List<SelectionCriteria> result = selectionCriteriaService.getAppliedCriteria(criteriaIdList);
            return ResponseEntity.ok(result);
        }catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
