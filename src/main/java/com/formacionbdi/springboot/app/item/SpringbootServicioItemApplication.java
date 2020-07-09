package com.formacionbdi.springboot.app.item;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.formacionbdi.springboot.app.item.varenum.VariablesEnum;

@EnableCircuitBreaker
@EnableEurekaClient
@EnableFeignClients
@SpringBootApplication
public class SpringbootServicioItemApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicioItemApplication.class, args);
		
		String[] array = new String[2]; 
		array[0] = "1";
		array[1] = "2";
//		array[2] = "3";
		
		System.out.println(array[0]);
		System.out.println(array[1]);
//		System.out.println(array[2]);
		System.err.println(VariablesEnum.TITULO.getValue());
	}

}
