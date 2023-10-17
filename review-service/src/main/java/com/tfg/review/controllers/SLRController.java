package com.tfg.review.controllers;

import com.tfg.review.dtos.*;
import com.tfg.review.exceptions.SLRNotFoundException;
import com.tfg.review.models.Researcher;
import com.tfg.review.models.SLR;
import com.tfg.review.repositories.SLRRepository;
import com.tfg.review.services.*;
import com.tfg.review.services.implementations.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/review")
@Validated
@AllArgsConstructor
@Tag(name = "Revisiones Sistemáticas(SLR)", description = "Esta API contiene todas las operaciones que pueden ser " +
        "realizadas sobre una revisión sistemática en la aplicación.")
@SecurityRequirement(name="Bearer Authentication")
public class SLRController {

    private final SLRService SLRService;
    private final SLRRepository sLRRepository;
    private final ResearcherService researcherService;
    private final ProtocolService protocolService;
    private final KeywordService keywordService;
    private final SelectionCriteriaServiceImpl selectionCriteriaService;
    private final SnowballingService snowballingService;
    private final CustomDataSourceService customDataSourceService;
    private final PredefDataSourceService predefDataSourceService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener todas las revisiones sistemáticas de la base de datos",
            description = "Este método obtiene todas las revisiones sistemáticas de la base de datos",
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
                            description = "No Content",
                            responseCode = "204",
                            content = @Content
                    ),
                    @ApiResponse(
                    description = "Internal Server Error",
                    responseCode = "500"
                )
            }
    )
    public ResponseEntity<List<FullSLRDTO>> findAll(){
        List<SLR> result  = SLRService.findAll();
        if(result.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(result.stream().map(slr -> FullSLRDTO.buildFromEntity(slr)).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Método para obtener una SLR",
            description = "Este método obtiene un revisión sistemática a través de su ID en la base de datos",
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
                            description = "El ID no existe en la base de datos",
                            responseCode = "400",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            responseCode = "500"
                    )
            }
    )
    public ResponseEntity<FullSLRDTO> findOne(@PathVariable Long id){
        try {
            SLR result = SLRService.findOne(id);

            return ResponseEntity.ok(FullSLRDTO.buildFromEntity(result));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Crear una nueva revisión sistemática",
            description = "Este método permite crear una nueva revisión asociada al ID de un usuario, que queda almacenado como investigador principal de la revisión."
            +" Este método no es llamado directamente desde esta API, si no que es llamado indirectamente desde el servicio de usuarios, cuando un usuario registrado crea una revisión. ",
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

                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            responseCode = "500"
                    )
            }
    )
    @PostMapping("/{userId}/create-review")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SLRDTO> createFromUser(@PathVariable Long userId, @Valid @RequestBody ResearcherAndSLR fullDto, BindingResult binding) {
        if(binding.hasErrors()){
            return new ResponseEntity(binding.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            UserDTO userDTO = fullDto.getUserDTO();
            Researcher researcher = researcherService.createFromUser(userDTO, userId);
            SLRDTO slrDTO = fullDto.getSlrDTO();
            return ResponseEntity.ok(SLRService.createAndSave(slrDTO, researcher.getId()));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/edit")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Editar una revisión sistemática de literatura",
            description = "Este método permite editar la revisión que está asociada al ID de la ruta",
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
    public ResponseEntity<FullSLRDTO> edit(@PathVariable Long id, @Valid @RequestBody SLRDTO dto, BindingResult binding) {
        if(binding.hasErrors()){
            return new ResponseEntity(binding.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            SLR result = SLRService.findOne(id);
            result.setTitle(dto.getTitle());
            result.setDescription(dto.getDescription());
            result.setWorkField(dto.getWorkField());
            result.setObjective(dto.getObjective());
            result.setPublicVisibility(dto.getPublicVisibility());
            return ResponseEntity.ok(SLRService.update(result));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Eliminar una revisión sistemática de literatura",
            description = "Este método permite eliminar la revisión que está asociada al ID de la ruta",
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
    public ResponseEntity<SLR> deleteSLR(@PathVariable Long id){

        if(!sLRRepository.existsById(id)){
            try {
                throw new SLRNotFoundException("Element not found");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }
        SLRService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping("/byResearcherUserId/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener todas las revisiones sistemáticas de un usuario",
            description = "Este método obtiene todas las revisiones sistemáticas en las que un usuario actúa como investigador",
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
                            description = "El ID no existe en la base de datos",
                            responseCode = "400",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Internal Server Error",
                            responseCode = "500"
                    )
            }
    )
    public ResponseEntity<List<SLRDTO>> getByResearcher(@PathVariable Long userId){
       try{
           List<SLR> slrs = SLRService.findByResearcherUserId(userId);
           List<SLRDTO> dtos = slrs.stream().map(slr -> SLRDTO.buildFromEntity(slr)).collect(Collectors.toList());
           return ResponseEntity.ok(dtos);
       }catch (Exception e) {
           return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
       }
    }

    @PostMapping("/{slrId}/add-protocol")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Añadir el protocolo a la revisión sistemática",
            description = "Este método permite crear un protocolo y añadirlo a la revisión que está asociada al ID de la ruta",
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
    public ResponseEntity<FullProtocolDTO> createProtocol(@PathVariable Long slrId, @Validated @RequestBody CreateProtocolDTO dto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return new ResponseEntity(bindingResult.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            SLR slr = SLRService.findOne(slrId);
            if(slr.getProtocol()!=null) throw new IllegalArgumentException("This SLR already has a protocol. Edit it!");

            ProtocolDTO protocolDTO = dto.getProtocolDTO();
            Set<KeywordDTO> keywords = dto.getKeywords();
            Set<SelectionCriteriaDTO> criteria = dto.getCriteria();
            Set<DataSourceDTO> dataSources = dto.getDataSources();

            FullProtocolDTO result = protocolService.createAndSave(protocolDTO,slrId);

            if(!keywords.isEmpty()){
                for(KeywordDTO keyword : keywords){
                    keywordService.createAndSave(keyword,result.getId());
                }
            }
            if(!criteria.isEmpty()){
                for(SelectionCriteriaDTO criterion : criteria){
                    selectionCriteriaService.createAndSave(criterion,result.getId());
                }
            }

            if(!dataSources.isEmpty()){
                for(DataSourceDTO dataSource : dataSources){
                    if(dataSource.getSnowballingType()!=null){
                        SnowballingDTO snowballing = SnowballingDTO.buildFromDataSource(dataSource);
                        snowballingService.createAndSave(snowballing, result.getId());
                    }else if(dataSource.getUrl()!=null){
                        PredefDataSourceDTO predefDataSource = PredefDataSourceDTO.buildFromDataSource(dataSource);
                        predefDataSourceService.createAndSave(predefDataSource, result.getId());
                    }else {
                        CustomDataSourceDTO customDataSource = CustomDataSourceDTO.buildFromDataSource(dataSource);
                        customDataSourceService.createAndSave(customDataSource, result.getId());
                    }
                }
            }
            return ResponseEntity.ok(FullProtocolDTO.buildFromEntity(protocolService.findOne(result.getId())));

        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
