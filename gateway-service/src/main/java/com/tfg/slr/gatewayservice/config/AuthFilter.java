package com.tfg.slr.gatewayservice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.slr.gatewayservice.dto.ErrorDTO;
import com.tfg.slr.gatewayservice.dto.TokenDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config>{

    private WebClient.Builder webClient;

    public AuthFilter(WebClient.Builder webClient){
        super(Config.class);
        this.webClient = webClient;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((((exchange, chain) -> {
            log.info("**************************************************************************");
            log.info("Request URL: " + exchange.getRequest().getURI().getPath());
            //if the request doesn't contain the header "Authorization" returns an error with the code unauthorized
            if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.info("Error 401 Unauthorized. No auth header for request: "+ exchange.getRequest().getURI().getPath());
                return onError(exchange,HttpStatus.UNAUTHORIZED.toString(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), "Error 401 Unauthorized. No auth header", HttpStatus.UNAUTHORIZED);
            }
              //Extracts the authorization header from the request
               String tokenHeader =  exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
               String[] subStrings = tokenHeader.split(" "); //splits the header by whitespaces
               if(subStrings.length!=2 || !subStrings[0].equals("Bearer")){ //if there's not 2 items of the header is not Bearer returns error
                   log.info("Error 401 Unauthorized. Invalid or revoked token for request: "+ exchange.getRequest().getURI().getPath());
                   return onError(exchange,HttpStatus.UNAUTHORIZED.toString(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), "Invalid or expired token", HttpStatus.UNAUTHORIZED);
               }else{
                   //Tries to validate the token calling the user service API with the method validate
                       return webClient.build().post().uri("http://user-service/api/login/validate?token="+subStrings[1])
                               .retrieve().bodyToMono(TokenDTO.class).map(dto -> {
                                   dto.getToken();
                                   return exchange;
                               }).flatMap(chain::filter)
                               .onErrorResume(throwable ->{
                                   // Handle the error of getting an invalid token
                                   if (throwable instanceof WebClientResponseException) {
                                       WebClientResponseException responseException = (WebClientResponseException) throwable;
                                       HttpStatus status = responseException.getStatusCode();
                                       // If the error is a 401 Unauthorized
                                       if (status == HttpStatus.UNAUTHORIZED) {
                                           log.info("Error 401 Unauthorized. Invalid or revoked token for request: "+ exchange.getRequest().getURI().getPath());
                                           return onError(exchange,HttpStatus.UNAUTHORIZED.toString(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                                       }
                                   }
                                   // If the error sin 500 internal server error
                                   log.info("500 - Internal server error. One server is not responding for request: "+ exchange.getRequest().getURI().getPath());
                                   return onError(exchange, HttpStatus.INTERNAL_SERVER_ERROR.toString(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "One server is not responding. If you just run a service, try again. If not, try login again.", HttpStatus.INTERNAL_SERVER_ERROR);
                               });
               }
        })));
    }

    public Mono<Void> onError(ServerWebExchange exchange, String errorCode, String errorMessage, String details, HttpStatus status){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        ErrorDTO errorData = new ErrorDTO(errorCode,errorMessage,details);
        try {
            return response.writeWith(Mono.just(new ObjectMapper().writeValueAsBytes(errorData)).map(value -> response.bufferFactory().wrap(value)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response.setComplete();
    }
    public static class Config {

    }
}
