package com.tfg.slr.searchservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.jbibtex.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.io.FileNotFoundException;

@SpringBootApplication
@EnableEurekaClient
@OpenAPIDefinition
public class ReviewMicroserviceApplication {

	public static void main(String[] args) throws FileNotFoundException, ParseException {

		SpringApplication.run(ReviewMicroserviceApplication.class, args);

		//BibTeXparser.parser("C:\\Users\\pedro\\Downloads\\scopus.bib");

	}

}
