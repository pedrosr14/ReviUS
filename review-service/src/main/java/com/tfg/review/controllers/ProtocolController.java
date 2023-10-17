package com.tfg.review.controllers;

import com.tfg.review.dtos.*;
import com.tfg.review.exceptions.DataSourceNotFoundException;
import com.tfg.review.exceptions.KeywordNotFoundException;
import com.tfg.review.exceptions.SelectionCriteriaNotFoundException;
import com.tfg.review.models.*;
import com.tfg.review.services.*;
import com.tfg.review.services.implementations.*;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/review/protocol")
@Validated
@AllArgsConstructor
@Tag(name = "Protocolos", description = "Esta API contiene las operaciones que pueden ser " +
        "realizadas sobre el protocolo de una revisión.")
@SecurityRequirement(name="Bearer Authentication")
public class ProtocolController {

    private final ProtocolServiceImpl protocolService;
    private final KeywordService keywordService;
    private final SelectionCriteriaServiceImpl selectionCriteriaService;
    private final DataSourceService dataSourceService;
    private final SnowballingService snowballingService;
    private final CustomDataSourceService customDataSourceService;
    private final PredefDataSourceService predefDataSourceService;
    private final FormService formService;
    private final FormFieldService formFieldService;

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener todos los protocolos existentes.",
            description = "Este método permite consultar a la base de datos todos los protocolos que hay almacenados.",
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
    public ResponseEntity<List<FullProtocolDTO>> getAll(){
        List<Protocol> protocols = protocolService.findAll();
        return ResponseEntity.ok(protocols.stream().map(FullProtocolDTO::buildFromEntity).collect(Collectors.toList()));
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Obtener un protocolo.",
            description = "Este método permite consultar a la base de datos para extraer un protolo por su ID.",
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
    public ResponseEntity<FullProtocolDTO> getOne(@PathVariable Long id){
        try{
            Protocol protocol = protocolService.findOne(id);
            return ResponseEntity.ok(FullProtocolDTO.buildFromEntity(protocol));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }
    @PutMapping("/{id}/edit")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Editar el protocolo de una revisión sistemática.",
            description = "Este método permite modificar los campos de un protocolo perteneciente a una revisión sistemática " +
                    "de literatura.",
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
    public ResponseEntity<FullProtocolDTO> editProtocol(@PathVariable Long id, @Validated @RequestBody CreateProtocolDTO dto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return new ResponseEntity(bindingResult.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            Protocol protocol = protocolService.findOne(id);
            ProtocolDTO protocolDTO = dto.getProtocolDTO();

            //--Edit protocol--//
            protocol.setPrincipalQuestion(protocolDTO.getPrincipalQuestion());
            protocol.setSecondaryQuestion(protocolDTO.getSecondaryQuestion());
            protocol.setLanguages(protocolDTO.getLanguages());

            //--Add new keywords--//
            Set<KeywordDTO> keywordDTO = dto.getKeywords();
            if(!keywordDTO.isEmpty()){
                for (KeywordDTO keyword : keywordDTO){
                    keywordService.createAndSave(keyword,protocol.getId());
                }
            }

            //--Add new selection criteria--//
            Set<SelectionCriteriaDTO> newCriteria = dto.getCriteria();
            if(!newCriteria.isEmpty()){
                for (SelectionCriteriaDTO criterion : newCriteria){
                    selectionCriteriaService.addCriteria(criterion,protocol.getId());
                }
            }
            //--Add new data sources--//
            Set<DataSourceDTO> newDataSources = dto.getDataSources();
            if(!newDataSources.isEmpty()) {
                for (DataSourceDTO newDataSource : newDataSources) {

                    if (newDataSource.getSnowballingType() != null) {
                        SnowballingDTO snowballing = SnowballingDTO.buildFromDataSource(newDataSource);
                        snowballingService.createAndSave(snowballing, protocol.getId());
                    } else if (newDataSource.getUrl() != null) {
                        PredefDataSourceDTO predefDataSource = PredefDataSourceDTO.buildFromDataSource(newDataSource);
                        predefDataSourceService.createAndSave(predefDataSource, protocol.getId());
                    } else {
                        CustomDataSourceDTO customDataSource = CustomDataSourceDTO.buildFromDataSource(newDataSource);
                        customDataSourceService.createAndSave(customDataSource, protocol.getId());
                    }
                }
            }
            protocol = protocolService.update(protocol);
            FullProtocolDTO result = FullProtocolDTO.buildFromEntity(protocol);
            return new ResponseEntity(result, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{protocolId}/edit-keyword")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Editar las palabras clave de un protocolo.",
            description = "Este método permite editar palabras clave asociadas a un protocolo.",
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
    public ResponseEntity<FullProtocolDTO> editKeyword(@RequestParam("keywordId") Long keywordId, @PathVariable Long protocolId, @Valid @RequestBody KeywordDTO dto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return new ResponseEntity(bindingResult.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            Protocol protocol = protocolService.findOne(protocolId);
            Keyword result = keywordService.findOne(keywordId).orElseThrow(()-> new KeywordNotFoundException("Keyword not found"));

            keywordService.deleteFromProtocol(result, protocolId);
            keywordService.createAndSave(dto, protocolId);
            return ResponseEntity.ok(FullProtocolDTO.buildFromEntity(protocol));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{protocolId}/delete-keyword")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Eliminar palabras clave de un protocolo.",
            description = "Este método permite eliminar palabras clave asociadas a un protocolo.",
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
    public ResponseEntity<FullProtocolDTO> deleteKeyword(@RequestParam("keywordId") Long keywordId, @PathVariable Long protocolId) {
        try{
            Protocol protocol = protocolService.findOne(protocolId);
            Keyword result = keywordService.findOne(keywordId).orElseThrow(()-> new KeywordNotFoundException("Keyword not found"));

            keywordService.deleteFromProtocol(result, protocolId);
            return ResponseEntity.ok(FullProtocolDTO.buildFromEntity(protocol));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/{protocolId}/edit-criteria")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Editar los criterios de selección de un protocolo.",
            description = "Este método permite editar los criterios de selección asociados a un protocolo.",
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
    public ResponseEntity<FullProtocolDTO> editSelectionCriteria(@RequestParam("selectionCriteriaId") Long criteriaId, @PathVariable Long protocolId, @Valid @RequestBody SelectionCriteriaDTO dto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return new ResponseEntity(bindingResult.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            Protocol protocol = protocolService.findOne(protocolId);
            SelectionCriteria result = selectionCriteriaService.findOne(criteriaId).orElseThrow(()-> new SelectionCriteriaNotFoundException("Selection criteria not found"));

            selectionCriteriaService.deleteFromProtocol(criteriaId, protocolId);
            selectionCriteriaService.createAndSave(dto, protocolId);
            return ResponseEntity.ok(FullProtocolDTO.buildFromEntity(protocol));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{protocolId}/delete-criteria")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Eliminar criterios de selección de un protocolo",
            description = "Este método permite eliminar criterios de seleeción asociados a un protocolo.",
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
    public ResponseEntity<FullProtocolDTO> deleteSelectionCriteria(@RequestParam("selectionCriteriaId") Long criteriaId, @PathVariable Long protocolId) {
        try{
            Protocol protocol = protocolService.findOne(protocolId);
            SelectionCriteria result = selectionCriteriaService.findOne(criteriaId).orElseThrow(()-> new SelectionCriteriaNotFoundException("Selection criteria not found"));

            selectionCriteriaService.deleteFromProtocol(criteriaId, protocolId);
            return ResponseEntity.ok(FullProtocolDTO.buildFromEntity(protocol));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{protocolId}/edit-data-source")
    @Operation(
            summary = "Editar una fuente de datos perteneciente a un protocolo.",
            description = "Este método permite editar una fuente de datos asociada a un protocolo.",
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
    public ResponseEntity<FullProtocolDTO> editDataSource(@RequestParam("dataSourceId") Long dataSourceId, @PathVariable Long protocolId, @Valid @RequestBody DataSourceDTO dto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return new ResponseEntity(bindingResult.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            Protocol protocol = protocolService.findOne(protocolId);

            if (dto.getSnowballingType() != null) {
                Snowballing snowballing = snowballingService.findOne(dataSourceId).orElseThrow(()-> new DataSourceNotFoundException("Data source not found"));
                SnowballingDTO snowballingDTO = SnowballingDTO.buildFromDataSource(dto);
                snowballingService.deleteFromProtocol(dataSourceId, protocolId);
                snowballingService.createAndSave(snowballingDTO, protocol.getId());
            } else if (dto.getUrl() != null) {
                PredefDataSource dataSource = predefDataSourceService.findOne(dataSourceId).orElseThrow(()-> new DataSourceNotFoundException("Data source not found"));
                PredefDataSourceDTO predefDataSource = PredefDataSourceDTO.buildFromDataSource(dto);
                predefDataSourceService.deleteFromProtocol(dataSourceId, protocolId);
                predefDataSourceService.createAndSave(predefDataSource, protocol.getId());
            } else {
                CustomDataSource dataSource = customDataSourceService.findOne(dataSourceId).orElseThrow(()-> new DataSourceNotFoundException("Data source not found"));
                CustomDataSourceDTO customDataSource = CustomDataSourceDTO.buildFromDataSource(dto);
                customDataSourceService.deleteFromProtocol(dataSourceId, protocolId);
                customDataSourceService.createAndSave(customDataSource, protocol.getId());
            }

            return ResponseEntity.ok(FullProtocolDTO.buildFromEntity(protocol));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{protocolId}/delete-data-source")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Eliminar una fuente de datos de un protocolo.",
            description = "Este método permite eliminar una fuente de datos asociada a un protocolo.",
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
    public ResponseEntity<FullProtocolDTO> deleteDataSource(@RequestParam("dataSourceId") Long dataSourceId, @PathVariable Long protocolId) {
        try{
            Protocol protocol = protocolService.findOne(protocolId);
            DataSource dataSource = dataSourceService.findOne(dataSourceId).orElseThrow(()-> new DataSourceNotFoundException("Data source not found"));
           if(dataSource instanceof PredefDataSource){
               predefDataSourceService.deleteFromProtocol(dataSourceId,protocolId);
            }else if(dataSource instanceof CustomDataSource){
               customDataSourceService.deleteFromProtocol(dataSourceId,protocolId);
            }else if(dataSource instanceof Snowballing){
                snowballingService.deleteFromProtocol(dataSourceId, protocolId);
            }
            return ResponseEntity.ok(FullProtocolDTO.buildFromEntity(protocol));
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{protocolId}/add-extraction-form/")
    @Operation(
            summary = "Agregar un formulario de extracción a un protocolo.",
            description = "Este método permite editar una fuente de datos asociada a un protocolo.",
            responses = {
                    @ApiResponse(
                            description = "Created",
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
    public ResponseEntity<FullFormDTO> addExtractionForm(@PathVariable Long protocolId, @Validated @RequestBody FormCreationDTO creationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.toString(), HttpStatus.BAD_REQUEST);
        }
        try {
            List<FormFieldDTO> formFieldDTOS = creationDTO.getFieldDTOS();
            Form form = new Form();
            form.setFormType(FormType.EXTRACTION);
            form.setFormFields(new ArrayList<>());
            Form savedForm = formService.createAndSave(form, protocolId);

            for (FormFieldDTO fieldDTO : formFieldDTOS) {
                FormField field = FormField.builder().fieldName(fieldDTO.getFieldName()).fieldType(fieldDTO.getFieldType()).build();
                formFieldService.createAndSave(field, savedForm.getId());
            }
            FullFormDTO result = FullFormDTO.buildFromEntity(formService.findOne(savedForm.getId()).get());
            return new ResponseEntity(result,HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{protocolId}/get-selection-criteria")
    @Operation(
            summary = "Obtener todos los criterios de selección asociados a un protocolo.",
            description = "Este método permite obtener un listado con todos los criterios de selección de un protocolo.",
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
    public ResponseEntity<List<SelectionCriteriaDTO>> getSelectionCriteria(@PathVariable Long protocolId){
        try {
            List<SelectionCriteriaDTO> selectionCriteriaDTOS = protocolService.findSelectionCriteriaFromSearch(protocolId).stream().map(s -> SelectionCriteriaDTO.buildFromEntity(s)).collect(Collectors.toList());
            return new ResponseEntity<>(selectionCriteriaDTOS, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{protocolId}/get-form-data/{formType}")
    @Operation(
            summary = "Visualizar los campos de un formulario.",
            description = "Este método hacer una consulta a la base de datos para extraer un formulario junto a todos " +
                    "sus campos para poder aplicarlo.",
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
    public ResponseEntity<?> getFormData(@PathVariable Long protocolId, @PathVariable FormType formType){
        try{
            List<FormFieldDTO> result = formService.findFormWithFields(protocolId,formType);
            return ResponseEntity.ok(result);
        }catch(Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
