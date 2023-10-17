package com.tfg.slr.searchservice.controllers;

import com.tfg.slr.searchservice.services.FormInstanceService;
import com.tfg.slr.searchservice.utils.MessageConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/form-instance")
@Tag(name = "Instancias de Formulario", description = "Contiene las operaciones relacionadas con las instancias creadas a " +
        "partir de los formularios definidos en el protocolo.")
@SecurityRequirement(name="Bearer Authentication")
public class FormInstanceController {

    private FormInstanceService formInstanceService;

    @DeleteMapping("/full-delete/{formInstanceId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Eliminar por completo una instancia de formulario.",
            description = "Este método elimina un formulario junto a todos sus campos.",
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
                            responseCode = "500",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<String> fullDelete(@PathVariable Long formInstanceId){
        try{
            formInstanceService.deleteFromProtocol(formInstanceId);
            return ResponseEntity.ok(MessageConstants.DELETE_SUCCESS);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(MessageConstants.DELETE_FAIL);
        }
    }
}
