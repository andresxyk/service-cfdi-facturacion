package com.gda.cfdi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;


@Configuration
public class SwaggerConfig {

	@Bean
	  public OpenAPI usersMicroserviceOpenAPI() {
	    return new OpenAPI()
	        .info(new Info().title("Spring + Api CFDI")
	            .description(
	                "Api Creacion CFDI")
	            .version("1.0"));
	  }
}
