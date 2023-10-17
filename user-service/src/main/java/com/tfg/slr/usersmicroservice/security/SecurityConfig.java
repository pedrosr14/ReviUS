package com.tfg.slr.usersmicroservice.security;

import com.tfg.slr.usersmicroservice.services.implementations.AuthUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

/**
 * This class configures Spring Security. Spring Security Core dependencies must be added to pom.xml.
 * It's been implemented following the rules of Spring 5.7.0, which deprecates extending WebSecurityConfigurerAdapter
 * We enable method security, which supports authorization at method level. Allows restricting what roles can execute a method
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final AuthUserService authUserService;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;

    private static final String[] AUTH_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    };

    public SecurityConfig(JwtProvider jwtProvider, AuthUserService authUserService, PasswordEncoder passwordEncoder, TokenBlacklistService tokenBlacklistService) {
        this.jwtProvider = jwtProvider;
        this.authUserService = authUserService;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Bean
    public JwtTokenFilter jwtFilter() {

        return new JwtTokenFilter(jwtProvider, authUserService, tokenBlacklistService);
    }

   @Bean //This method sets the authentication context, replaces the configureGlobal method of the deprecated version
   public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
       return http.getSharedObject(AuthenticationManagerBuilder.class)
               .userDetailsService(authUserService)
               .passwordEncoder(passwordEncoder)
               .and()
               .build();
   }

    /*
        This is the method that replaces the deprecated void configure(HttpSecurity) for HTTP request management
        Allows to configure better the security and, instead of modify the HttpSecurity object,
        it and returns a SecurityFilterChain with all the security configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       /*Enables CORS mechanism, that allows cross communication between domains,
        like between apis or api with front-end application in different domains*/
        http.cors().and().
                csrf().disable() //Disables CSRF security, that is not needed when using JWT authentication instead of cookies to keep session alive
                .authorizeRequests() //here starts the configuration for the requests
                .antMatchers("/api/admin/**").hasAnyRole("ADMIN") //allows access only to users with ROLE_ADMIN
                .antMatchers("/api/login/**").anonymous()//allows access only to users not authenticated
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()//any other requests must come from authenticated users
                .and().httpBasic() //Configures basic HTTP authentication
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //The API won't create nor maintain sessions alive.
                .and() // Session management depends on JWT authentication tokens
                .logout().logoutUrl("/api/logout")
                .logoutSuccessHandler(((request, response, authentication) -> {
                            response.setHeader("Authorization", "revoked");
                            response.setStatus(HttpServletResponse.SC_OK);
                            SecurityContextHolder.clearContext();
                        }
                       ));

        //Last,we apply the jwtFilter that we have defined in the JwtTokenFilter class before the basic username and password filter
        http.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


}
