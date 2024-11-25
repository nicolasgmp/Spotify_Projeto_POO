package br.com.poofinal.bands_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@EnableFeignClients
@OpenAPIDefinition(info = @Info(title = "Spotify Projeto POO", version = "1.0", description = "Aplicação MVC desenvolvida para a disciplina de POO"))
@SpringBootApplication
public class BandsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BandsApiApplication.class, args);
	}

}
