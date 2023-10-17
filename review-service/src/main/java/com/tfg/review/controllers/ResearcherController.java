package com.tfg.review.controllers;

import com.tfg.review.dtos.ResearcherAndSLR;
import com.tfg.review.dtos.ResearcherDTO;
import com.tfg.review.dtos.SLRDTO;
import com.tfg.review.dtos.UserDTO;
import com.tfg.review.models.Researcher;
import com.tfg.review.services.ResearcherService;
import com.tfg.review.services.SLRService;
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

@RestController
@AllArgsConstructor
@RequestMapping("/api/review/researcher")
@Validated
@Tag(name = "Investigadores", description = "Esta API contiene las operaciones que pueden ser " +
        "aplicadas sobre un investigador.")
@SecurityRequirement(name="Bearer Authentication")
public class ResearcherController {

    private final ResearcherService researcherService;
    private final SLRService slrService;

    @PostMapping("/add-to-SLR")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Añadir un investigador a una revisión sistemática",
            description = "Este método toma los datos de un usuario del sistema y lo añade como investigador colaborador a " +
                    "la revisión sistemática indicada.",
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
    public ResponseEntity<ResearcherDTO> addCollaborator(@Valid @RequestBody ResearcherAndSLR fullDto, BindingResult binding) {
        if (binding.hasErrors()) {
            return new ResponseEntity(binding.toString(), HttpStatus.BAD_REQUEST);
        }
        try {
            SLRDTO slrdto = fullDto.getSlrDTO();
            UserDTO researcherDTO = fullDto.getUserDTO();
            Researcher result = slrService.addResearcher(slrdto.getId(), researcherDTO, researcherDTO.getId());
            ResearcherDTO resultDTO = ResearcherDTO.builder().id(result.getId()).rol(result.getRol()).name(result.getName()).userId(researcherDTO.getId()).build();
            return ResponseEntity.ok(resultDTO);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
