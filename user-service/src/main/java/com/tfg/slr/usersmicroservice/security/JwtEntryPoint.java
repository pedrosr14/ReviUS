package com.tfg.slr.usersmicroservice.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** This class is a Spring Component and implements the AuthenticationEntryPoint
 *  Has the purpose of managing exceptions related to Jwt authentication errors
 *  Throws an exception if an unauthenticated user try to access protected resources
 */
@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {

    //Need this to register and show error messages in the system log
    private final static Logger logger = LoggerFactory.getLogger(JwtEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        logger.error("Error in the JWT entry point. Unauthorized");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
