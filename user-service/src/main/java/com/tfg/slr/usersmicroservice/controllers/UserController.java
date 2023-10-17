package com.tfg.slr.usersmicroservice.controllers;

import com.tfg.slr.usersmicroservice.dtos.*;
import com.tfg.slr.usersmicroservice.exceptions.ErrorDeletingAccountException;
import com.tfg.slr.usersmicroservice.exceptions.LoadProfileException;
import com.tfg.slr.usersmicroservice.exceptions.NullEntityException;
import com.tfg.slr.usersmicroservice.exceptions.UserNotFoundException;
import com.tfg.slr.usersmicroservice.models.User;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.services.implementations.AuthUserService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@Tag(name = "Usuarios", description = "Contiene las operaciones que puede realizar un usuario registrado.")
@SecurityRequirement(name="Bearer Authentication")
public class UserController {

    public final UserService userService;
    public final AuthUserService authUserService;
    public final UserAccountService userAccountService;

    @Operation(
            summary = "Obtener todos los perfiles de usuario.",
            description = "Este método permite visualizar todos los perfiles de usuario que existen en la base de datos.",
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
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAll(){
        List<UserDTO> dtos = userService.findAll().stream().map(user -> UserDTO.fromEntity(user)).collect(Collectors.toList());

        if(dtos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(dtos);
    }
    @Operation(
            summary = "Obtener el perfil de un usuario por su ID.",
            description = "Este método permite el perfil de un usuario dado su ID en la base de datos.",
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
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {

        Optional<User> result = userService.findById(userId);

        if(result.isPresent()) {
            return ResponseEntity.ok().body(UserDTO.fromEntity(result.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @Operation(
            summary = "Crear mi perfil de usuario.",
            description = "Este método permite al usuario registrado crear su perfil de usuario.",
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
                            responseCode = "500",
                            content = @Content
                    )
            }
    )
    @PostMapping("/create-profile")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?>createProfile(@Valid @RequestBody UserDTO dto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest().body(bindingResult.toString());
        }else {
            try {
                UserProfileDTO user = userService.createAndSave(dto);
                return ResponseEntity.status(HttpStatus.CREATED).body(user);
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

    @Operation(
            summary = "Actualizar el perfil de usuario.",
            description = "Este método permite al usuario registrado actualizar o modificar su perfil de usuario.",
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
    @PutMapping("/{id}/update")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserProfileDTO> updateProfile(@PathVariable("id") Long id, @Valid @RequestBody UserDTO dto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return new ResponseEntity(bindingResult.toString(), HttpStatus.BAD_REQUEST);
        }else {
            try {
                User user = userService.findById(id).orElseThrow(()->new UserNotFoundException(MessageConstants.USER_NOT_FOUND));
                UserAccount principal = authUserService.getAuthUserAccount();
                if(Objects.equals(principal.getId(), user.getUserAccount().getId())){
                    user.setCompleteName(dto.getName());
                    user.setWorkField(dto.getWorkField());
                    user.setInstitution(dto.getInstitution());
                    user.setEmail(dto.getEmail());
                    userService.update(user);
                    return ResponseEntity.ok(new UserProfileDTO(user, user.getUserAccount()));
                }else{
                    return new ResponseEntity("El ID proporcionado no coincide con el de "+principal.getUserName(),HttpStatus.BAD_REQUEST);
                }

            }catch (Exception e){
                return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        }
    }
    @Operation(
            summary = "Eliminar mi perfil de usuario.",
            description = "Este método permite al usuario registrado eliminar su perfil de usuario junto a su cuenta de usuario.",
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
    @DeleteMapping("/{id}/delete-profile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        try{
            userService.delete(id);
            SecurityContextHolder.getContext().setAuthentication(null);

            return new ResponseEntity<>(MessageConstants.PROFILE_DELETED, HttpStatus.OK);
        }catch (Exception e){
            throw new ErrorDeletingAccountException(MessageConstants.DELETING_ERROR);
        }
    }

    @Operation(
            summary = "Acceder a mis revisiones sistemáticas.",
            description = "Este método permite al usuario registrado listar las revisiones sistemáticas de literatura" +
                    " que ha creado y en las que participa.",
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
                            responseCode = "500",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "No content",
                            responseCode = "204",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{userId}/my-reviews")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<SLRDTO>> getSLRs(@PathVariable Long userId){
        try{
           List<SLRDTO> result = userService.getSLR(userId);
                if(result.isEmpty()){
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                }
            return ResponseEntity.ok(result);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Crear una revisión sistemática de literatura.",
            description = "Este método permite al usuario registrado crear una revisión sistemática de literatura" +
                    " en la que aparecerá como investigador principal.",
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
                            description = "Internal Server Error",
                            responseCode = "500",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    )
            }
    )
    @PostMapping("/{userId}/review/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<SLRDTO> createFromUser(@PathVariable Long userId, @Valid @RequestBody ResearcherAndSLR dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.toString(), HttpStatus.BAD_REQUEST);
        }
        try{
            SLRDTO result = userService.createSLR(dto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Acceder a mi perfil.",
            description = "Este método permite al usuario registrado visualizar su perfil de usuario.",
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
                            responseCode = "500",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404",
                            content = @Content
                    )
            }
    )
    @GetMapping("/my-profile")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FullUserProfileDTO> getProfile(){
        try {
            UserAccount authUserAccount = authUserService.getAuthUserAccount();
            User authUser = authUserAccount.getUser();
            return ResponseEntity.ok(FullUserProfileDTO.buildProfile(authUser,authUserAccount));
        }catch (Exception e){
           throw new LoadProfileException(MessageConstants.ERROR_LOADING_PROFILE);
        }
    }

    @PutMapping("/review/{slrId}/add-researcher/{email}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Añadir investigadores a una revisión.",
            description = "Este método permite al usuario registrado, que sea además investigador principar, añadir otros " +
                    "investigadores como colaboradores a la revisión haciendo uso del email.",
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
                            responseCode = "500",
                            content = @Content
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<ResearcherDTO> addCollaborator(@PathVariable String email, @PathVariable Long slrId){
        try {
            ResearcherDTO result = userService.addCollaborator(email, slrId);
            return ResponseEntity.ok(result);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
