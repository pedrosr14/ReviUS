package com.tfg.review.controllers;

import com.tfg.review.dtos.KeywordDTO;
import com.tfg.review.exceptions.KeywordNotFoundException;
import com.tfg.review.models.Keyword;
import com.tfg.review.services.KeywordService;
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

@AllArgsConstructor
@RestController
@RequestMapping("/api/review/keyword")
@Validated
@Tag(name = "Palabras clave", description = "Esta API contiene las operaciones que pueden ser " +
        "realizadas sobre las palabras clave de un protocolo.")
@SecurityRequirement(name="Bearer Authentication")
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Visualizar todas las palabras clave.",
            description = "Este método permite consultar a la base de datos todos las palabras clave que hay almacenadas.",
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
    public ResponseEntity<List<KeywordDTO>> findAll(){
        List<Keyword> keywords = keywordService.findAll();
        return ResponseEntity.ok(
                keywords.stream().map(keyword -> KeywordDTO.buildFromEntity(keyword)).collect(Collectors.toList()));
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener una palabra clave.",
            description = "Este método permite obtener una palabra clave haciendo uso de si ID en la base de datos.",
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
    public ResponseEntity<KeywordDTO> findOne(@PathVariable Long id){
        Keyword result = keywordService.findOne(id).orElseThrow(()-> new KeywordNotFoundException("Keyword not found"));
        KeywordDTO resultDTO = KeywordDTO.buildFromEntity(result);
        return ResponseEntity.ok(resultDTO);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Agregar nuevas palabras clave a un protocolo.",
            description = "Este método permite generar una nueva palabra clave y, en caso de no existir aún, añadirla al protocolo. " +
                    "En caso de estar repetida, no dejará añadirla.",
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
    public ResponseEntity<KeywordDTO> createAndSaveKeyword(@PathParam("protocolId") Long protocolId, @Valid @RequestBody KeywordDTO dto, BindingResult binding) {
        if(binding.hasErrors()){
            return new ResponseEntity(binding.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            Keyword result = keywordService.createAndSave(dto, protocolId);
            return ResponseEntity.ok(KeywordDTO.buildFromEntity(result));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/edit")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<KeywordDTO> editKeyword(@PathVariable Long id, @Valid @RequestBody KeywordDTO dto, BindingResult binding) {
        if(binding.hasErrors()){
            return new ResponseEntity(binding.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            Keyword keyword = keywordService.findOne(id).orElseThrow(() -> new KeywordNotFoundException("KeywordNotFound"));
            return ResponseEntity.ok(KeywordDTO.buildFromEntity(keywordService.update(keyword)));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Eliminar una palabra clave de la base de datos.",
            description = "Este método permite eliminar una palabra clave proporcionando su ID.",
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
    public ResponseEntity<String> deleteKeyword(@PathVariable Long id){
        try{
            keywordService.delete(id);
            return ResponseEntity.ok("Keyword deleted");
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
