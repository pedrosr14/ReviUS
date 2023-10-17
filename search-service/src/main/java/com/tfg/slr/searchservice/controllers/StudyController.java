package com.tfg.slr.searchservice.controllers;

import com.tfg.slr.searchservice.dtos.FormInstanceDTO;
import com.tfg.slr.searchservice.dtos.SelectionCriteriaDTO;
import com.tfg.slr.searchservice.dtos.StudyDTO;
import com.tfg.slr.searchservice.exceptions.ItemsNotFoundException;
import com.tfg.slr.searchservice.models.FormType;
import com.tfg.slr.searchservice.models.Study;
import com.tfg.slr.searchservice.services.FormInstanceService;
import com.tfg.slr.searchservice.services.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/study")

@Tag(name = "Estudios", description = "Contiene las operaciones relacionadas con los estudios encontrados en las búsquedas.")
@SecurityRequirement(name="Bearer Authentication")
public class StudyController {

    private final StudyService studyService;
    private final FormInstanceService formInstanceService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener todos los estudios.",
            description = "Este método obtiene y muestra todos los estudios que existen en la base de datos.",
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
                            description = "No content",
                            responseCode = "204",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            responseCode = "500",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<List<StudyDTO>> getAll(){
        try{
            List<Study> studies = studyService.findAll();
            if(studies.isEmpty()){
                throw new ItemsNotFoundException("No items were found");
            }
            return ResponseEntity.ok(studies.stream().map(s -> StudyDTO.buildFromEntity(s)).collect(Collectors.toList()));
        }catch (Exception e) {
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener un estudio de la base de datos.",
            description = "Este método obtiene y muestra la información de un estudio localizado en la base de datos con un ID.",
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
                            responseCode = "500",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<StudyDTO> getStudy(@PathVariable Long id){
        try{
            Study study = studyService.findOne(id);
            return ResponseEntity.ok(StudyDTO.buildFromEntity(study));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}/selection-criteria")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Visualizar los criterios de selección.",
            description = "Este obtiene los criterios de selección desde el servicio de revisiones y permite visualizar " +
                    "los datos para poder realizar la clasificación.",
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
                            responseCode = "500",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<List<SelectionCriteriaDTO>> selectionCriteria(@PathVariable Long id){
        try{
            List<SelectionCriteriaDTO> selectionCriteria = studyService.getSelectionCriteria(id);
            return ResponseEntity.ok(selectionCriteria);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(
            summary = "Aplicar formularios.",
            description = "Este método obtiene los formularios asociados a un protocolo sobre el que se ha realizado " +
                    "una búsqueda en la que se ha obtenido el estudio.",
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
                            responseCode = "500",
                            content = @Content
                    )
            }
    )
    @PostMapping("/{studyId}/extraction-form")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> extractionFormInstance(@PathVariable Long studyId){
        try{
            FormInstanceDTO result = formInstanceService.createAndSave(FormType.EXTRACTION, studyId);
            return ResponseEntity.ok(result);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/select")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Seleccionar estudio.",
            description = "Este método obtiene modificar el estado de selección de un estudio.",
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
                            responseCode = "500",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<StudyDTO> selectStudy(@PathParam("id") Long id){
        try {
            return ResponseEntity.ok(studyService.acceptStudy(id));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/reject")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Rechazar estudio.",
            description = "Este método obtiene modificar el estado de selección a rechazado de un estudio.",
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
                            responseCode = "500",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<StudyDTO> rejectStudy(@PathParam("id") Long id){
        try {
            return ResponseEntity.ok(studyService.rejectStudy(id));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/duplicated")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Marcar como duplicado.",
            description = "Este método permite marcar como duplicado un artículo.",
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
                            responseCode = "500",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<StudyDTO> markAsDuplicated(@PathParam("id") Long id){
        try {
            return ResponseEntity.ok(studyService.markAsDuplicated(id));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }


}
