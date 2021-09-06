package com.maltadev.cursoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = {"com.maltadev.cursoapi.model"})
@ComponentScan(basePackages = {"com.maltadev.cursoapi"})
@EnableJpaRepositories(basePackages = {"com.maltadev.cursoapi.*"})
@EnableTransactionManagement
@EnableWebMvc
@RestController
@EnableAutoConfiguration
@EnableCaching
public class CursoApiApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(CursoApiApplication.class, args);		
//		System.out.println(new BCryptPasswordEncoder().encode("123"));
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		
		registry.addMapping("/usuario/**")
		.allowedMethods("*")
		.allowedOrigins("*");
		
		registry.addMapping("/profissao/**")
		.allowedMethods("*")
		.allowedOrigins("*");
		
		registry.addMapping("/recuperar/**")
		.allowedMethods("*")
		.allowedOrigins("*");
		
		
		//.allowedOrigins("www.cliente40.com.br") libera para cliente especifico
		//.allowedOrigins("www.cliente40.com.br", "www.cliente41.com.br") libera para varios clientes especificos
		
		//Liberar por requisições
		//registry.addMapping("/usuario/**").allowedMethods("POST", "PUT", "DELETE");
		
	}

}
