package com.formacionbdi.springboot.app.item.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.builders.ApiInfoBuilder;
 import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
//con esta anotacion habilitamos swagger para que documente nuestra aplicacion
@EnableSwagger2
public class SwaggerConfig {
//http://localhost:8005/v2/api-docs
	//url ui swagger http://localhost:8005/swagger-ui.html
	
	@Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          //.apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))              
          .apis(RequestHandlerSelectors.any())
          .build().pathMapping("/").apiInfo(apiInfo());                                           
    }
	
	//con este metodo pasamos informacion adicional al servi
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Items-swager").description("api items swagger").version("1.0.0")
				.build();
	}
	
//	@Bean
//    public Docket api() { 
//        return new Docket(DocumentationType.SWAGGER_2)  
//          .select()                                  
//          .apis(RequestHandlerSelectors.any())              
//          .paths(PathSelectors.any())                          
//          .build();                                           
//    }
	
	
}
