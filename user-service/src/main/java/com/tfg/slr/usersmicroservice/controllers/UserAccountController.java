package com.tfg.slr.usersmicroservice.controllers;

import com.tfg.slr.usersmicroservice.dtos.NewPasswordDTO;
import com.tfg.slr.usersmicroservice.dtos.UserAccountDTO;
import com.tfg.slr.usersmicroservice.exceptions.IncorrectPasswordException;
import com.tfg.slr.usersmicroservice.exceptions.UserAccountNotFoundException;
import com.tfg.slr.usersmicroservice.exceptions.WrongUsernameException;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.services.implementations.AuthUserService;
import com.tfg.slr.usersmicroservice.services.UserAccountService;
import com.tfg.slr.usersmicroservice.utils.MessageConstants;
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
import java.util.Objects;

@RestController
@AllArgsConstructor
@RequestMapping("api/user/account/")
@Tag(name = "Cuentas de Usuario", description = "Contiene todas las operaciones que pueden ser" +
        "realizadas sobre una cuenta de usuario en la aplicación.")
@SecurityRequirement(name="Bearer Authentication")
public class UserAccountController {

    private final UserAccountService userAccountService;
    private final AuthUserService authUserService;

    @Operation(
            summary = "Obtener todas las cuentas de usuario",
            description = "Este método permite visualizar todas las cuentas de usuario que existen en la base de datos.",
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
    ResponseEntity<List<UserAccount>> getAll(){

        List<UserAccount> userAccounts =userAccountService.findAll();

        if(userAccounts.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userAccounts);
    }

    @Operation(
            summary = "Obtener una cuenta de usuario por su ID",
            description = "Este método permite obtener una cuenta de usuario de la base de datos indicando su ID.",
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
    @GetMapping("/{id}")
    public ResponseEntity<? extends Object> getOneById(@PathVariable Long id){

        try{
            UserAccount result = userAccountService.findOne(id);

            return ResponseEntity.ok(result);
        }catch (Exception e) {
            throw new UserAccountNotFoundException(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND);
        }
    }
    @Operation(
            summary = "Cambiar nombre de usuario",
            description = "Este método permite cambiar el nombre de usuario del usuario logeado verificando su contraseña.",
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
    @PutMapping("/update-username")
    public ResponseEntity<String> updateProfile(@Valid @RequestBody UserAccountDTO userAccountDTO, BindingResult binding) {
        if(binding.hasErrors()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(binding.toString());
        }
        try{
            UserAccount authUserAccount = authUserService.getAuthUserAccount();
            if(authUserService.validatePassword(userAccountDTO.getPassword())){
                authUserAccount.setUserName(userAccountDTO.getUsername());
                userAccountService.update(authUserAccount);
                return ResponseEntity.ok(MessageConstants.USERNAME_CHANGED);
            }else{
                throw new IncorrectPasswordException(MessageConstants.PASSWORD_HAS_ERRORS);
            }
        }catch (UserAccountNotFoundException e) {
            throw new UserAccountNotFoundException(MessageConstants.USER_ACCOUNT_NULL_NOT_FOUND);
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @Operation(
            summary = "Modificar contraseña",
            description = "Este método permite modificar la contraseña del usuario logeado tras verificar la contraseña actual.",
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
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody NewPasswordDTO newPasswordDTO, BindingResult binding) {
        if (binding.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(binding.toString());
        }
        try {
            UserAccount authUserAccount = authUserService.getAuthUserAccount();
            if (!Objects.equals(authUserAccount.getUserName(), newPasswordDTO.getUsername())) {
                throw new WrongUsernameException(MessageConstants.WRONG_USERNAME);
            }else {
                userAccountService.changePassword(authUserAccount.getId(), newPasswordDTO.getOldPassword(), newPasswordDTO.getNewPassword());
                return ResponseEntity.ok(MessageConstants.PASSWORD_CHANGED);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
