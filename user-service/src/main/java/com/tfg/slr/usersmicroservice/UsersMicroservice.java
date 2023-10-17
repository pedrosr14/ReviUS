package com.tfg.slr.usersmicroservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
@EnableEurekaClient
@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "API ReviUS - Gestión de Usuarios",
        version = "1.0",
        description = "Esta API contiene las operaciones relacionadas con la gestión de usuarios, incluyendo autenticación y autorización.",
        contact = @Contact(
                name = "Pedro Serrano Ramos",
                email = "pedserram@alum.us.es"
        )
    )
)
public class UsersMicroservice {

    public static void main(String[] args) {

        SpringApplication.run(UsersMicroservice.class, args);
    }

}
