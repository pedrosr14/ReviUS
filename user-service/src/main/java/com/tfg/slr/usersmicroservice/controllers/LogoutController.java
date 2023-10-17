package com.tfg.slr.usersmicroservice.controllers;

import com.tfg.slr.usersmicroservice.security.JwtProvider;
import com.tfg.slr.usersmicroservice.security.JwtTokenFilter;
import com.tfg.slr.usersmicroservice.security.TokenBlacklistService;
import com.tfg.slr.usersmicroservice.utils.MessageConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
@Tag(name = "Logout", description = "Este controlador permite el cierre de la sesión activa del usuario registrado.")
@SecurityRequirement(name="Bearer Authentication")
@RequestMapping("/api/logout")
public class LogoutController {
    private final TokenBlacklistService blacklistService;

    @Operation(
            summary = "Cerrar sesión.",
            description = "Este método permite a un usuario cerrar la sesión activa, revocando su token de autorización.",
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
    @PostMapping("/")
    public ResponseEntity<String> logout(HttpServletRequest request){
        try{
            String header = request.getHeader("Authorization");
            if(header != null && header.startsWith("Bearer")) {
                blacklistService.addToBlacklist(header.replace("Bearer ", ""));
                return ResponseEntity.ok(MessageConstants.LOGOUT_SUCCESS);
            }else{
                throw new IllegalArgumentException("Header not found");
            }
        } catch (Exception e){
            return ResponseEntity.badRequest().body(MessageConstants.LOGOUT_FAILED);
        }
    }

}
