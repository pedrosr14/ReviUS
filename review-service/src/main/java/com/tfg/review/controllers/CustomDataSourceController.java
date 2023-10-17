package com.tfg.review.controllers;

import com.tfg.review.dtos.CustomDataSourceDTO;
import com.tfg.review.exceptions.DataSourceNotFoundException;
import com.tfg.review.models.CustomDataSource;
import com.tfg.review.models.Protocol;
import com.tfg.review.services.CustomDataSourceService;
import com.tfg.review.services.implementations.ProtocolServiceImpl;
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
@AllArgsConstructor
@RestController
@RequestMapping("/api/review/custom-data-source")
@Tag(name = "Fuentes de datos personalizadas", description = "Esta API contiene las operaciones que pueden ser " +
        "realizadas respecto a las fuentes de datos que un usuariop puede añadir al sistema.")
@SecurityRequirement(name="Bearer Authentication")
public class CustomDataSourceController {

    CustomDataSourceService customDataService;
    ProtocolServiceImpl protocolService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener todas las fuentes de datos personalizadas de la base de datos.",
            description = "Este método nos permite extraer todas las fuentes de datos personalizadas del sistema.",
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
    public ResponseEntity<List<CustomDataSourceDTO>> getAll() {
        List<CustomDataSource> dataSources = customDataService.findAll();
        return ResponseEntity.ok(dataSources.stream().map(dataSource -> CustomDataSourceDTO.buildFromEntity(dataSource)).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener una fuente de datos personalizada",
            description = "Este método nos permite extraer todas las fuentes de datos personalizadas del sistema.",
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
    public ResponseEntity<CustomDataSourceDTO> findOne(@PathVariable Long id) {
        try {
            CustomDataSource dataSource = customDataService.findOne(id).orElseThrow(() -> new DataSourceNotFoundException("DataSource not found"));
            return ResponseEntity.ok(CustomDataSourceDTO.buildFromEntity(dataSource));
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Generar una nueva base de datos personalizada",
            description = "Este método nos permite crear una nueva fuente de datos y asociarla a un protocolo.",
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
    public ResponseEntity<CustomDataSourceDTO> create(@PathParam("protocolId") Long protocolId, @Valid @RequestBody CustomDataSourceDTO dto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity(bindingResult.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            Protocol protocol = protocolService.findOne(protocolId);
            CustomDataSource dataSource = CustomDataSourceDTO.buildEntity(dto);
            return ResponseEntity.ok(CustomDataSourceDTO.buildFromEntity(dataSource));
        }catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
