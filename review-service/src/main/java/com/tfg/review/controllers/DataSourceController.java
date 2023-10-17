package com.tfg.review.controllers;

import com.tfg.review.dtos.*;
import com.tfg.review.exceptions.DataSourceNotFoundException;
import com.tfg.review.models.CustomDataSource;
import com.tfg.review.models.DataSource;
import com.tfg.review.models.PredefDataSource;
import com.tfg.review.models.Snowballing;
import com.tfg.review.services.CustomDataSourceService;
import com.tfg.review.services.DataSourceService;
import com.tfg.review.services.PredefDataSourceService;
import com.tfg.review.services.SnowballingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/api/review/data-source")
@Tag(name = "Fuentes de datos", description = "Esta API contiene las operaciones que pueden ser " +
        "realizadas con las fuentes de datos.")
@SecurityRequirement(name="Bearer Authentication")
public class DataSourceController {

    private DataSourceService dataSourceService;
    private CustomDataSourceService customDataSourceService;
    private PredefDataSourceService predefDataSourceService;
    private SnowballingService snowballingService;

    @Operation(
            summary = "Obtener todas las fuentes de datos de la base de datos.",
            description = "Este método permite extraer todas las fuentes de datos de la base de datos.",
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
                            description = "Internal Server Error",
                            responseCode = "500"
                    )
            }
    )
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<DataSourceDTO>> getAll() {
        List<DataSource> dataSources = dataSourceService.findAll();
        return ResponseEntity.ok(dataSources.stream().map(dataSource -> DataSourceDTO.buildFromEntity(dataSource)).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener una fuente de datos..",
            description = "Este método nos permite obtener una de las fuentes de datos que exiten almacenadas.",
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
    public ResponseEntity<? extends Object> findOne(@PathVariable Long id){
        try{

            if(customDataSourceService.findOne(id).isPresent()){
                CustomDataSource dataSource = customDataSourceService.findOne(id).orElseThrow(()-> new DataSourceNotFoundException("DataSource not found"));
                CustomDataSourceDTO customDTO = CustomDataSourceDTO.buildFromEntity(dataSource);
                return ResponseEntity.ok(customDTO);
            }else if(predefDataSourceService.findOne(id).isPresent()){
                PredefDataSource dataSource = predefDataSourceService.findOne(id).orElseThrow(()-> new DataSourceNotFoundException("DataSource not found"));
                PredefDataSourceDTO predefDTO = PredefDataSourceDTO.buildFromEntity(dataSource);
                return ResponseEntity.ok(predefDTO);
            }else if(snowballingService.findOne(id).isPresent()){
                Snowballing dataSource = snowballingService.findOne(id).orElseThrow(()-> new DataSourceNotFoundException("DataSource not found"));
                SnowballingDTO snowballingDTO = SnowballingDTO.buildFromEntity(dataSource);
                return ResponseEntity.ok(snowballingDTO);
            }
            return ResponseEntity.badRequest().build();
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/new-search")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Realizar una nueva búsqueda asociada a una fuente de datos",
            description = "Este método nos permite, tras seleccionar una fuente de datos, iniciar una búsqueda para la revisión.",
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
    public ResponseEntity<SearchDTO> newSearch (@PathVariable Long id, @Valid @RequestBody SearchDTO dto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity(bindingResult.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            SearchDTO result = dataSourceService.createSearch(dto,id);
            return ResponseEntity.ok(result);
        }catch(Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
