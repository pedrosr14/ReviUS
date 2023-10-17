package com.tfg.slr.usersmicroservice.controllers;

import com.tfg.slr.usersmicroservice.dtos.TokenDTO;
import com.tfg.slr.usersmicroservice.dtos.UserAccountDTO;
import com.tfg.slr.usersmicroservice.models.UserAccount;
import com.tfg.slr.usersmicroservice.security.JwtProvider;
import com.tfg.slr.usersmicroservice.services.implementations.AuthUserService;
import com.tfg.slr.usersmicroservice.services.UserAccountService;
import com.tfg.slr.usersmicroservice.utils.MessageConstants;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/login")
@Tag(name = "Login", description = "Contiene las operaciones para registrarse como nuevo usuario y acceder a la aplicación.")
@SecurityRequirement(name="Bearer Authentication")
public class LoginController {

    private final UserAccountService userAccountService;
    private final JwtProvider jwtProvider;
    private final AuthUserService authUserService;
    @Operation(
            summary = "Registrarse como nuevo usuario.",
            description = "Este método permite a un usuario no registrado registrarse como nuevo usuario.",
            responses = {
                    @ApiResponse(
                            description = "CREATED",
                            responseCode = "201"
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
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<? extends Object> registerUserAccount(@Valid @RequestBody UserAccountDTO accountDTO, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(bindingResult.toString(), HttpStatus.BAD_REQUEST);
        }else {
            try {
                UserAccount result = userAccountService.registerUserAccount(accountDTO);
                return new ResponseEntity<>(MessageConstants.ACCOUNT_CREATED + result.getUserName(), HttpStatus.CREATED);
            } catch (Exception e) {
                ResponseEntity<String> errorResponse = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
                return errorResponse;
            }
        }
    }
    @Operation(
            summary = "Acceder con un usuario registrado.",
            description = "Este método permite acceder al sistema a un usuario registrado usando sus credenciales.",
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
                    )
            }
    )
    @PostMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> login (@Valid @RequestBody UserAccountDTO loginUserAccount, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ResponseEntity(MessageConstants.INVALID_USERNAME_OR_PASSWORD,HttpStatus.UNAUTHORIZED);
        }
        Authentication authentication = userAccountService.authenticate(loginUserAccount);
        /*We set the authentication object obtained from the user account into the security context
          This informs Spring that the auth user is authenticated*/
        SecurityContextHolder.getContext().setAuthentication(authentication);
        //Now we create the token with the auth data
        String jwt = jwtProvider.generateToken(authentication);
        TokenDTO tokenDTO = new TokenDTO(jwt);
        return new ResponseEntity<>(tokenDTO, HttpStatus.OK);
    }

    @PostMapping("/validate")
    @Hidden
    public ResponseEntity<TokenDTO> validate(@RequestParam String token){
        try{
            TokenDTO tokenDTO = authUserService.validate(token);
            if(tokenDTO == null){
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(tokenDTO);
        }catch (Exception e) {
            return new ResponseEntity(MessageConstants.INVALID_TOKEN, HttpStatus.BAD_REQUEST);
        }
    }
}
