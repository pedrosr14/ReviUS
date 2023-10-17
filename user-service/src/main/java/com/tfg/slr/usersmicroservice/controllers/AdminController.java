package com.tfg.slr.usersmicroservice.controllers;

import com.tfg.slr.usersmicroservice.dtos.UserDTO;
import com.tfg.slr.usersmicroservice.exceptions.UserNotFoundException;
import com.tfg.slr.usersmicroservice.models.User;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.services.UserAccountService;
import com.tfg.slr.usersmicroservice.services.UserService;
import com.tfg.slr.usersmicroservice.utils.MessageConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Administradores", description = "Este controlador contiene las operaciones que puede realizar un administrador.")
@SecurityRequirement(name="Bearer Authentication")
public class AdminController {

    private final UserService userService;
    private final UserAccountService userAccountService;
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    @Operation(
            summary = "Obtener los perfiles de todos los administradores del sistema.",
            description = "Este método permite visualizar los perfiles de todos los administradores del sistema.",
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
    public ResponseEntity<List<UserDTO>> getAll(){
        List<UserDTO> dtos = userService.findAll().stream().filter(user -> user.getUserAccount().getIsAdmin()).map(user -> UserDTO.fromEntity(user)).collect(Collectors.toList());

        if(dtos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(dtos);
    }
    @Operation(
            summary = "Eliminar el perfil de un usuario.",
            description = "Este método permite eliminar un perfil de usuario a otro usuario registrado como administrador del sistema.",
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
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user/{id}/delete-profile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        try {
            userService.delete(id);

            return ResponseEntity.ok(MessageConstants.PROFILE_DELETED);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Eliminar una cuenta de usuario.",
            description = "Este método permite eliminar una cuenta de usuario a otro usuario registrado como administrador del sistema.",
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
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/user-account/{id}/delete")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteAccount(@PathVariable("id") Long id) {
        try {
            userAccountService.delete(id);
            return new ResponseEntity<>(MessageConstants.ACCOUNT_DELETED, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @Operation(
            summary = "Modificar los permisos de un usuario.",
            description = "Este método permite a un usuario registrado como administrador dar o revocar permisos de administrador a otro usuario.",
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
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/user/{id}/change-authority")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> changeAuthorities(@PathVariable("id") Long id) {
        try {
            User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(MessageConstants.USER_NULL_NOT_FOUND));
            UserAccount userAccount = user.getUserAccount();
            if(userAccount.getIsAdmin()){
                userAccount.setIsAdmin(false);
            }else{
                userAccount.setIsAdmin(true);
            }
            userAccountService.update(userAccount);

            return new ResponseEntity<>(MessageConstants.AUTHORITY_CHANGED, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
