package com.tfg.slr.usersmicroservice.security;


import com.tfg.slr.usersmicroservice.models.AuthUser;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Date;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component //Because we'll need to inject it
public class JwtProvider {

    //Need this to register and show error messages in the system log
    private final static Logger logger = LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}") //1 day
    private int expirationInMs;

    public String generateToken(Authentication authentication){
        //The Authentication object contains the info of the authenticated user, we can obtain the user getting the Principal
        AuthUser authUser = (AuthUser) authentication.getPrincipal();

        //We obtain the roles from the GrantedAuthorities assigned to the authenticated user
        List<String> roles = authUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        //Time of creation and expiration of the token
        Instant now = Instant.now();
        Instant expirationTime = now.plusMillis(expirationInMs);

        /*Now we create the JWT object. To do this, jjwt must be added to the dependencies
         *The subject is a UNIQUE value that identifies the principal (Authenticated user)
         * IssuedAt is the time at which the token is issue
         * Expiration is the time after which the token will expire and will not be accepted
         * Finally, it is signed with the secret and a Hash512 algorithm
         */

        JwtBuilder builder = Jwts.builder().setSubject(authUser.getUsername()).
                setIssuedAt(Date.from(now)).setExpiration(Date.from(expirationTime)).
                signWith(SignatureAlgorithm.HS512, secret);

        //If the authUser has roles, we add it as a new claim to the token
        if(!roles.isEmpty()){
            builder.claim("roles", roles);
        }

        return builder.compact();
    }

    /**In this method we use the reverse operation of Jwts.builder and,
     * if any exception is thrown, we register it in the log
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**In this method, we use the reverse operation of builder as well,
     *but in this case, we also get the body and the subject of the token
     * the subject in this case is the username that we want
     */
    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        } catch (Exception e) {
        logger.error("Couldn't obtain the username from the subject", e);
        throw new JwtException("Couldn't obtain the username from the subject", e);
        }
    }
}
