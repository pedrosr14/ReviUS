package com.tfg.slr.searchservice.controllers;

import com.tfg.slr.searchservice.dtos.SearchDTO;
import com.tfg.slr.searchservice.dtos.StudyDTO;
import com.tfg.slr.searchservice.models.Search;
import com.tfg.slr.searchservice.models.Study;
import com.tfg.slr.searchservice.services.SearchService;
import com.tfg.slr.searchservice.services.StudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/search")
@Tag(name = "Búsquedas", description = "Contiene las operaciones relacionadas con las búsquedas de estudios.")
@SecurityRequirement(name="Bearer Authentication")
public class SearchController {

    private final SearchService searchService;
    private final StudyService studyService;
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener todas las búsquedas.",
            description = "Este método obtiene y muestra todas las búsuqedas que existen en la base de datos.",
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
    public ResponseEntity<List<SearchDTO>> getAll(){
        try{
            return ResponseEntity.ok(searchService.findAll().stream().map(search -> SearchDTO.buildFromEntity(search)).collect(Collectors.toList()));
        }catch (Exception e){
        return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener una búsqueda de la base de datos.",
            description = "Este método permite visualizar una búsqueda introduciendo us ID.",
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
    public ResponseEntity<SearchDTO> getOne(@PathVariable Long id){
        try{
            Search search = searchService.findOne(id);
            return ResponseEntity.ok(SearchDTO.buildFromEntity(search));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{dataSourceId}/create-search")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Generar una nuevá búsqueda.",
            description = "Este método permite la creación de una nueva búsqueda y asociarla al protocolo",
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
    public ResponseEntity<SearchDTO> createSearch(@PathVariable Long dataSourceId, @Valid @RequestBody SearchDTO creationDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity(bindingResult.toString(),HttpStatus.BAD_REQUEST);
        }
        try{
            Search result = searchService.createAndSave(dataSourceId, creationDto);
            return ResponseEntity.ok(SearchDTO.buildFromEntity(result));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{searchId}/add-study")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<StudyDTO> addStudy(@PathVariable Long searchId, @Valid @RequestBody StudyDTO studyDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity(bindingResult.toString(),HttpStatus.BAD_REQUEST);
        }
        try{
            Study result = studyService.createAndSave(searchId, studyDTO);
            StudyDTO response = StudyDTO.buildFromEntity(result);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }


}
