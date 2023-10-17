package com.tfg.slr.usersmicroservice.security;

import com.tfg.slr.usersmicroservice.services.implementations.AuthUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class extends OncePerRequestFilter, because it checks once per request, that the token is valid
 * If the token is valid, allows access to the resource of the request. If not, throws exception
 * Makes use of the JwtProvider to validate the token
 */
public class JwtTokenFilter extends OncePerRequestFilter {

    //Uses the logger to register in the log the errors thrown in the filter
    private final static Logger logger = LoggerFactory.getLogger(JwtTokenFilter.class);

    private final JwtProvider jwtProvider;
    private final AuthUserService authUserService;
    private final TokenBlacklistService tokenBlacklistService;

    public JwtTokenFilter(JwtProvider jwtProvider, AuthUserService authUserService, TokenBlacklistService tokenBlacklistService){

        this.jwtProvider = jwtProvider;
        this.authUserService = authUserService;
        this.tokenBlacklistService = tokenBlacklistService;
    }
    /*
    We need a method to obtain the token from the header of the HTTP request
      First, we obtain the Authorization header
      If it's not null and not empty, and starts with tha word "Bearer " (including the space)
      then return the token that is just after that space. If not, returns null
     */
    private String getToken(HttpServletRequest request){

        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
    /*
       This method intercepts all HTTP requests, executing everytime the server receives a request
       First, checks if the token exists and if so, validates it
       Then, gets the username from the token using the jwtProvider and the UserDetails associated to that username
       After that, a UsernamePasswordAuthenticationToken object is created with the details of the authenticated user, a null password and the roles of the user.
       Finally, sets the Authentication object in the security context with the data and authorities from the token of the authenticated user
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            String token = getToken(request);

            if(token != null && jwtProvider.validateToken(token) && !tokenBlacklistService.isTokenRevoked(token)){

                String username = jwtProvider.getUsernameFromToken(token);
                UserDetails userDetails = authUserService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                //Password is unnecessary (null) because we are using JWT, and the password must be checked and authenticated before generating the token
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }
        }catch (Exception e){
            logger.error("doFilter method of JwtTokenFilter has failed" + e.getMessage());
        }

        //Calls the next filter in the chain of requests
        filterChain.doFilter(request, response);
    }
}
